package com.jhon.dogedex.api.dto

import com.jhon.dogedex.model.User

class UserDTOMapper {
    fun fromUserDTOToUserDomain(userDTO: UserDTO): User =
        User(
            userDTO.id,
            userDTO.email,
            userDTO.authenticationToken
        )
}