project(Lab1)



# target for server
add_executable(${PROJECT_NAME}_individual_server individual/src/server.cpp)
target_link_libraries(${PROJECT_NAME}_individual_server ${COMMON_DEPENDENCIES})

# target for client
add_executable(${PROJECT_NAME}_individual_client individual/src/client.cpp)
target_link_libraries(${PROJECT_NAME}_individual_client ${COMMON_DEPENDENCIES})



# add_executable(${PROJECT_NAME}_practical_server practical/src/server.cpp)
# target_link_libraries(${PROJECT_NAME}_practical_server ${COMMON_DEPENDENCIES})

# add_executable(${PROJECT_NAME}_practical_client practical/src/client.cpp)
# target_link_libraries(${PROJECT_NAME}_practical_client ${COMMON_DEPENDENCIES})
