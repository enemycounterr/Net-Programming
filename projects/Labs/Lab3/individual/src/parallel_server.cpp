#include <stdio.h>
#include <string.h>
#include <winsock2.h>
#include <windows.h>
#include <limits.h>

#pragma warning(disable : 4996)

#define DEFAULT_PORT 5553

CRITICAL_SECTION consoleLock;

void error_msg(const char *msg)
{
    EnterCriticalSection(&consoleLock);
    printf("[ERROR]: %s\n", msg);
    LeaveCriticalSection(&consoleLock);
}

DWORD WINAPI ClientHandler(LPVOID clientSocketParam)
{
    SOCKET client_socket = (SOCKET)clientSocketParam;

    sockaddr_in addr;
    int addr_len = sizeof(addr);
    getpeername(client_socket, (sockaddr *)&addr, &addr_len);
    char *str_addr = inet_ntoa(addr.sin_addr);

    EnterCriticalSection(&consoleLock);
    printf("Client connected from %s (Thread ID: %lu)!\n", str_addr, GetCurrentThreadId());
    LeaveCriticalSection(&consoleLock);

    while (1)
    {
        int count = 0;
        int ret = recv(client_socket, (char *)&count, sizeof(int), 0);

        if (ret <= 0)
            break;

        EnterCriticalSection(&consoleLock);
        printf("[%s] Incoming array size: %d\n", str_addr, count);
        LeaveCriticalSection(&consoleLock);

        if (count <= 0 || count > 10000)
        {
            continue;
        }

        int *numbers = new int[count];
        int total_bytes = count * sizeof(int);

        ret = recv(client_socket, (char *)numbers, total_bytes, 0);
        if (ret != total_bytes)
        {
            delete[] numbers;
            break;
        }

        int min_val = INT_MAX;
        int max_val = INT_MIN;
        long long sum = 0;

        EnterCriticalSection(&consoleLock);
        printf("[%s] Data: ", str_addr);
        for (int i = 0; i < count; i++)
        {
            int val = numbers[i];
            printf("%d ", val);
            if (val < min_val)
                min_val = val;
            if (val > max_val)
                max_val = val;
            sum += val;
        }
        printf("\n");
        LeaveCriticalSection(&consoleLock);

        double avg_val = (double)sum / count;

        char response[1024];
        sprintf(response,
                "Results from Thread %lu:\n -> Min: %d\n -> Max: %d\n -> Avg: %.2f",
                GetCurrentThreadId(), min_val, max_val, avg_val);

        send(client_socket, response, strlen(response), 0);

        delete[] numbers;
    }

    closesocket(client_socket);

    EnterCriticalSection(&consoleLock);
    printf("Client %s disconnected (Thread with id %lu finished).\n", str_addr, GetCurrentThreadId());
    LeaveCriticalSection(&consoleLock);

    return 0;
}

void server()
{
    short port = DEFAULT_PORT;
    struct sockaddr_in saddr = {};

    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = htonl(INADDR_ANY);
    saddr.sin_port = htons(port);

    SOCKET s = socket(AF_INET, SOCK_STREAM, 0);
    if (s == INVALID_SOCKET)
    {
        error_msg("Can't create socket");
        return;
    }
    if (bind(s, (sockaddr *)&saddr, sizeof(saddr)) != 0)
    {
        error_msg("Can't bind");
        return;
    }
    if (listen(s, 100) != 0)
    {
        error_msg("Can't listen");
        return;
    }

    InitializeCriticalSection(&consoleLock);

    printf("Parallel Server started on port %d.\n", port);

    while (1)
    {
        sockaddr_in client_addr = {};
        int len = sizeof(client_addr);

        // Головний потік тільки приймає з'єднання
        SOCKET new_socket = accept(s, (sockaddr *)&client_addr, &len);

        if (new_socket == INVALID_SOCKET)
        {
            error_msg("Can't accept connection");
            continue;
        }

        // ЗАПУСК ПОТОКУ
        // Замість виклику функції напряму (handle_connection), ми створюємо потік.
        // new_socket передаємо як параметр (LPVOID)
        HANDLE hThread = CreateThread(
            NULL,               // Атрибути безпеки (NULL - за замовчуванням)
            0,                  // Розмір стеку (0 - за замовчуванням)
            ClientHandler,      // Вказівник на функцію потоку
            (LPVOID)new_socket, // Параметр для функції (наш сокет)
            0,                  // Прапорці створення (0 - запустити негайно)
            NULL                // ID потоку (нам не потрібен, тому NULL)
        );

        if (hThread == NULL)
        {
            error_msg("Failed to create thread!");
            closesocket(new_socket);
        }
        else
        {
            CloseHandle(hThread);
        }
    }

    DeleteCriticalSection(&consoleLock);
    closesocket(s);
}

int main()
{
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
    server();
    WSACleanup();
    return 0;
}