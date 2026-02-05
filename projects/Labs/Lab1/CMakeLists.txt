project(Lab1)



# Ціль для Сервера
add_executable(${PROJECT_NAME}_individual_server individual/src/server.cpp)
target_link_libraries(${PROJECT_NAME}_individual_server ${COMMON_DEPENDENCIES})

# Ціль для Клієнта
add_executable(${PROJECT_NAME}_individual_client individual/src/client.cpp)
target_link_libraries(${PROJECT_NAME}_individual_client ${COMMON_DEPENDENCIES})


# ==========================================
# ЧАСТИНА 2: ПРАКТИЧНЕ ЗАВДАННЯ (Practical)
# ==========================================
# Тут може бути або один файл, або теж клієнт-сервер.
# Я зроблю універсальний варіант.


    
    # ПРИМІТКА: Якщо у практичній теж треба окремо Клієнт і Сервер,
    # то закоментуй рядки вище (з GLOB) і розкоментуй нижче:
    
# add_executable(${PROJECT_NAME}_practical_server practical/src/server.cpp)
# target_link_libraries(${PROJECT_NAME}_practical_server ${COMMON_DEPENDENCIES})

# add_executable(${PROJECT_NAME}_practical_client practical/src/client.cpp)
# target_link_libraries(${PROJECT_NAME}_practical_client ${COMMON_DEPENDENCIES})
