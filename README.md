# phrase-kotlin-client
Kotlin Client for PhraseApp 
## What is this?
This projects contain task to handle the synchronization via API from [PhraseApp API v2](http://docs.phraseapp.com/api/v2/).

[![Build Status][travis-image]][travis-url-main]
[![codecov][codecov-badge-url]][codecov-project-url]

[travis-image]: https://travis-ci.org/freenowtech/phrase-kotlin-client.svg?branch=master
[travis-url-main]: https://travis-ci.org/freenowtech/phrase-kotlin-client
[codecov-project-url]: https://codecov.io/gh/freenowtech/phrase-kotlin-client
[codecov-badge-url]: https://codecov.io/gh/freenowtech/phrase-kotlin-client/branch/master/graph/badge.svg

## How to use it

You need configure your sync task 
```
val config =  PhraseAppSyncTaskConfig(
    url = "https://api.phraseapp.com",
    authKey = "authKey"
)

val phraseAppSyncTask = PhraseAppSyncTask(config)
phraseAppSyncTask.run()
```
