package com.mytaxi.apis.phraseapi.client.config;

import org.aeonbits.owner.Config;

public interface TestConfig extends Config
{
    @DefaultValue("${ENV_PHRASE_AUTHTOKEN}")
    String authToken();

    @DefaultValue("${ENV_PHRASE_PROJECTID}")
    String projectId();

    @DefaultValue("${ENV_PHRASE_BRANCH}")
    String branch();

    @DefaultValue("${ENV_PHRASE_LOCALEID_DE}")
    String localeIdDe();

    @DefaultValue("${ENV_PHRASE_LOCALEID_DE_BRANCH}")
    String localeIdDeBranch();

    @DefaultValue("${ENV_PHRASE_HOST}")
    String host();
}