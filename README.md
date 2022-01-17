# phrase-kotlin-client
Kotlin Client for PhraseApp 
## What is this?
This project contains the task to handle the synchronization via API from [PhraseApp API v2](http://docs.phraseapp.com/api/v2/).

[![codecov][codecov-badge-url]][codecov-project-url]
[![Build](https://github.com/freenowtech/phrase-kotlin-client/actions/workflows/mvn.yml/badge.svg?branch=master)](https://github.com/freenowtech/phrase-kotlin-client/actions/workflows/mvn.yml)
[![Release](https://img.shields.io/github/v/release/freenowtech/phrase-kotlin-client)](https://github.com/freenowtech/phrase-kotlin-client/releases/latest)

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
