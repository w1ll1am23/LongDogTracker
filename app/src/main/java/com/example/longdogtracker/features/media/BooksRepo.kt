package com.example.longdogtracker.features.media

import androidx.annotation.StringRes
import com.example.longdogtracker.BuildConfig
import com.example.longdogtracker.R
import com.example.longdogtracker.features.media.network.GoogleBooksApi
import com.example.longdogtracker.features.media.ui.model.MediaType
import com.example.longdogtracker.features.media.ui.model.UiMedia
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingBooksFilter
import com.example.longdogtracker.features.settings.model.settingLastFetchBooksFromService
import com.example.longdogtracker.room.BookDao
import com.example.longdogtracker.room.RoomBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class BooksRepo @Inject constructor(
    private val googleBooksApi: GoogleBooksApi,
    private val bookDao: BookDao,
    private val settingsPreferences: SettingsPreferences,
) {

    suspend fun getAllBooks(ignoreFilters: Boolean = false): GetBooksResult {
        var fetchedFromService = false
        return withContext(Dispatchers.IO) {
            val showBooks = settingsPreferences.readBooleanPreference(
                settingBooksFilter
            )
            var books = bookDao.getAll()

            val lastServiceFetch =
                settingsPreferences.readLongPreference(settingLastFetchBooksFromService)
            val shouldRefresh = lastServiceFetch?.let {
                val then = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                val now = ZonedDateTime.now(
                    ZoneId.systemDefault()
                )
                Duration.between(then, now).toDays() > 1
            } ?: true
            // The DB is empty need to fetch from the service
            if (books.isEmpty() || shouldRefresh) {

                fetchedFromService = true
                val result = googleBooksApi.getBooks(BuildConfig.GOOGLE_BOOKS_API_KEY).execute()
                if (result.isSuccessful) {
                    // Insert in to DB
                    result.body()?.items?.let { apiBooks ->
                        val booksToInsert = apiBooks.mapNotNull { book ->
                            if (books.find { it.apiId == book.id } == null && book.volumeInfo.readingModes.text) {
                                RoomBook(
                                    id = 0,
                                    apiId = book.id,
                                    title = book.volumeInfo.title,
                                    description = book.volumeInfo.description ?: "",
                                    imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://"),
                                    knownLongDogCount = 0,
                                    longDogsFound = 0,
                                    longDogLocations = null,
                                )
                            } else {
                                null
                            }
                        }
                        if (booksToInsert.isNotEmpty()) {
                            bookDao.insertAll(*booksToInsert.toTypedArray())
                            settingsPreferences.writeLongPreference(
                                settingLastFetchBooksFromService, ZonedDateTime.now(
                                    ZoneId.systemDefault()
                                ).toEpochSecond()
                            )
                        }
                    }
                } else {
                    GetBooksResult.Failure(R.string.error_unknown_issue_fetching_books)
                }

            }
            if (fetchedFromService) {
                books = bookDao.getAll()
            }
            GetBooksResult.Books(
                books.mapNotNull {
                    if (showBooks || ignoreFilters) {
                        UiMedia(
                            id = it.id,
                            apiId = it.apiId,
                            type = MediaType.Movie,
                            title = it.title,
                            description = it.description,
                            imageUrl = it.imageUrl,
                            knownLongDogCount = it.knownLongDogCount,
                            longDogsFound = it.longDogsFound,
                            longDogLocations = null // TODO: Fix this
                        )
                    } else {
                        null
                    }
                })
        }
    }

    suspend fun updateBook(uiBook: UiMedia) {
        withContext(Dispatchers.IO) {
            val movie = bookDao.getBookById(uiBook.id)
            val updatedMovie = movie.copy(
                longDogsFound = uiBook.longDogsFound,
                longDogLocations = uiBook.longDogLocations?.joinToString(";")
            )
            bookDao.updateBook(updatedMovie)
        }
    }

    sealed class GetBooksResult {
        data class Books(val books: List<UiMedia>) : GetBooksResult()
        data class Failure(@StringRes val errorMessage: Int) : GetBooksResult()
    }
}