"""
Generates a SQLite database for pre-populating the Room database.
"""

import json
import sqlite3


def main():
    con = sqlite3.connect("known_longdogs.db")
    cur = con.cursor()
    cur.execute('''
          CREATE TABLE IF NOT EXISTS longdogs
          ([longDogLocationId] INTEGER PRIMARY KEY NOT NULL, [seasonEpisode] TEXT NOT NULL, [location] TEXT NOT NULL, [found] INTEGER NOT NULL, [userAdded] INTEGER NOT NULL)
          ''')
    con.commit()

    locations_json_file = open("./known_longdogs.json", "r")
    locations_json = json.load(locations_json_file)

    for season, episodes in locations_json["seasons"].items():
        for episode, locations in episodes.items():
            location_count = 0
            for location in locations:
                location_id = int(season) * 1000 + int(episode) * 10 + location_count
                add_row(cur, location_id, str(int(season)) + str(int(episode)), location, False, False)
                location_count = location_count + 1
    con.commit()


def add_row(cursor, id, season_episode, location, found, user_added):
    cursor.execute("INSERT INTO longdogs VALUES(?, ?, ?, ?, ?)",
                   (id, season_episode, location, found, user_added))


main()
