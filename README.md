# phrase-kotlin-client
Kotlin Client for PhraseApp 
## What is this?
This projects contain client to handle the API from [PhraseApp API v2](http://docs.phraseapp.com/api/v2/).
It's supposed to expose Phrase Core within the kotlin world.

[![Build Status][travis-image]][travis-url-main]
[![codecov][codecov-badge-url]][codecov-project-url]

[travis-image]: https://travis-ci.org/freenowtech/phrase-kotlin-client.svg?branch=master
[travis-url-main]: https://travis-ci.org/freenowtech/phrase-kotlin-client
[codecov-project-url]: https://codecov.io/gh/freenowtech/phrase-kotlin-client
[codecov-badge-url]: https://codecov.io/gh/freenowtech/phrase-kotlin-client/branch/master/graph/badge.svg

## How to use it

You need configure your client 
```
val config =  PhraseApiClientConfig(
    url = "https://api.phraseapp.com",
    authKey = "authKey"
)

val phraseApiClient = PhraseApiClientImpl(config)
```
## Supported API
* Project
  * Create project
  * Get project by id
  * Get all projects
  * Delete project
  * Update project

* Locale
  * Create locale
  * Get all locales for project
  * Get locale by id
  * Delete locale
  * Download locale translations
  
* Translation
  * Create translation

* Key
  * Create key
  * Search key
  * Delete key
