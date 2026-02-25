#include "common_net.h"
#include "../include/own_protocol.h"
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
        ClientRequest req;
        memset(&req, 0, sizeof(ClientRequest));

        if (recv(client_socket, (char *)&req, sizeof(ClientRequest), 0) <= 0)
            break;

        EnterCriticalSection(&consoleLock);
        printf("[%s] Incoming array size: %d\n", str_addr, req.count);
        LeaveCriticalSection(&consoleLock);

        if (req.count <= 0 || req.count > 10000)
        {
            continue;
        }

        int min_val = INT_MAX;
        int max_val = INT_MIN;
        long long sum = 0;

        EnterCriticalSection(&consoleLock);
        printf("[%s] Data: ", str_addr);
        for (int i = 0; i < req.count; i++)
        {
            int val = req.data[i];
            printf("%d ", val);
            if (val < min_val)
                min_val = val;
            if (val > max_val)
                max_val = val;
            sum += val;
        }
        printf("\n");
        LeaveCriticalSection(&consoleLock);

        ServerResponse res;
        res.min = min_val;
        res.max = max_val;
        res.average = (double)sum / req.count;

        send(client_socket, (char *)&res, sizeof(ServerResponse), 0);
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

        SOCKET new_socket = accept(s, (sockaddr *)&client_addr, &len);

        if (new_socket == INVALID_SOCKET)
        {
            error_msg("Can't accept connection");
            continue;
        }

        HANDLE hThread = CreateThread(
            NULL,
            0,
            ClientHandler,
            (LPVOID)new_socket,
            0,
            NULL);

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
    common_init_handler();
    server();
    common_exit_handler();
    return 0;
}