| Functionality    | Method | Endpoints                                                  |
|------------------|--------|------------------------------------------------------------|
| Core             | POST   | /users                                                     | 
| Core             | POST   | /films                                                     | 
| Core             | POST   | /reviews                                                   |
| Core             | POST   | /directors                                                 |
| Core             | GET    | /directors                                                 |
| Core             | GET    | /users                                                     |
| Core             | GET    | /films                                                     |
| Core             | GET    | /genres                                                    |
| Core             | GET    | /mpa                                                       |
| Core             | GET    | /reviews                                                   |
| Core             | GET    | /genres/{id}                                               |
| Core             | GET    | /users/{id}                                                |
| Core             | GET    | /films/{id}                                                |
| Core             | GET    | /mpa/{id}                                                  |
| Core             | GET    | /directors/{id}                                            |
| Core             | GET    | /reviews/{id}                                              |
| Core             | PUT    | /directors                                                 |
| Core             | PUT    | /films                                                     |
| Core             | PUT    | /users                                                     |
| Core             | PUT    | /reviews                                                   |
| Core             | DELETE | /directors/{id}                                            |
| Core             | DELETE | /users/{id}                                                |
| Core             | DELETE | /films/{id}                                                |
| Core             | DELETE | /reviews/{id}                                              |
| Friendship       | PUT    | /users/{id}/friends/{friendId}                             |
| Friendship       | DELETE | /users/{id}/friends/{friendId}                             |
| Friendship       | GET    | /users/{id}/friends                                        |
| Friendship       | GET    | /users/{id}/friends/common/{otherId}                       |
| Like             | PUT    | /films/{id}/like/{userId}                                  |
| Like             | DELETE | /films/{id}/like/{userId}                                  |
| Like             | GET    | /films/popular?count={count}                               |
| Feed             | GET    | /users/{id}/feed                                           |
| Review and Grade | GET    | /reviews?filmId={filmId}&count={count}                     |
| Review and Grade | PUT    | /reviews/{id}/like/{userId}                                |
| Review and Grade | PUT    | /reviews/{id}/dislike/{userId}                             |
| Review and Grade | DELETE | /reviews/{id}/like/{userId}                                |
| Review and Grade | DELETE | /reviews/{id}/dislike/{userId}                             |
| Common films     | GET    | /films/common?userId={userId}&friendId={friendId}          |
| Search           | GET    | /films/search?query={search}&by=director,title             |
| Recommendations  | GET    | /users/{id}/recommendations                                |
| Other specific   | GET    | /films/popular?count={limit}&genreId={genreId}&year={year} |
| Other specific   | GET    | /films/director/{directorId}?sortBy=[year,likes]           |