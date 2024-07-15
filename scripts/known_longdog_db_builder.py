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
          ([id] INTEGER PRIMARY KEY NOT NULL, [season] INTEGER NOT NULL, [episode] INTEGER NOT NULL, [locations] TEXT NOT NULL)
          ''')
    con.commit()

    locations_json_file = open("./known_longdogs.json", "r")
    locations_json = json.load(locations_json_file)

    id = 0
    for season, episodes in locations_json["seasons"].items():
        for episode, locations in episodes.items():
            id += 1
            add_row(cur, id, int(season), int(episode), ";".join(locations))
    con.commit()

def add_row(cursor, id, season, episode, locations):
    cursor.execute("INSERT INTO longdogs VALUES(?, ?, ?, ?)",
                   (id, season, episode, locations))


main()
