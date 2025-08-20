package com.gabriel.danael_fernandes_react_manager.pay

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
//@ConfigurationProperties(prefix = "efibank.test")
class CredentialsEfiBank
{
    var client_id: String = "Client_Id_a6a1492476ce3feac8f36ff1c8e5e7b5e4979cec"
    var client_secret: String = "Client_Secret_965efe83f883a35ee4071889c3f53b205a9ac463"
    val certificate: String = "efi-bank-develop.p12"
}