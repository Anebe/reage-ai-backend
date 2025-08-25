package com.gabriel.danael_fernandes_react_manager.core.authentication

import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.UserRepresentation

data class UserAuth(
    val username: String,
    val email: String
) {
    constructor(userResource: UserRepresentation) : this(
        username = userResource.username,
        email = userResource.email,
    )

}


