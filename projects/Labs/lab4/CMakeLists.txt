project(Lab4)


#--------------------------------------------------------------------------------#
#PRACTICAL
#target for http_server
add_executable(${PROJECT_NAME}_http_server practical/http_server.cpp)
target_link_libraries(${PROJECT_NAME}_http_server ${COMMON_DEPENDENCIES})

#target for ftp_server
add_executable(${PROJECT_NAME}_ftp_server practical/ftp_server.cpp)
target_link_libraries(${PROJECT_NAME}_ftp_server ${COMMON_DEPENDENCIES})
#--------------------------------------------------------------------------------#



