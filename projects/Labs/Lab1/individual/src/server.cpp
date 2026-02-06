#include "common_net.h"

#pragma warning(disable : 4996)

#define DEFAULT_PORT 5553
#define CONNECTION_QUEUE 100

void error_msg(const char *msg)
{
    printf("[ERROR]: %s\n", msg);
}

void handle_connection(SOCKET client_socket, struct sockaddr_in *addr)
{
    char* str_addr = inet_ntoa(addr->sin_addr);
    printf("Client connected from %s!\n", str_addr);

    while (1)
    {
        int count = 0;
        int ret = recv(client_socket, (char*)&count, sizeof(int), 0);

        if (ret <= 0) {
            break; 
        }

        printf("[%s] Incoming array size: %d\n", str_addr, count);

        if (count <= 0 || count > 10000) {
            char err[] = "Invalid array size";
            send(client_socket, err, strlen(err), 0);
            continue;
        }

        int* numbers = new int[count];
        int total_bytes = count * sizeof(int);
        
        ret = recv(client_socket, (char*)numbers, total_bytes, 0);

        if (ret != total_bytes) {
            printf("[%s] Error receiving array data\n", str_addr);
            delete[] numbers;
            break;
        }

        int min_val = INT_MAX;
        int max_val = INT_MIN;
        long long sum = 0; 

        printf("[%s] Data: ", str_addr);
        for (int i = 0; i < count; i++) {
            int val = numbers[i];
            printf("%d ", val); 

            if (val < min_val) min_val = val;
            if (val > max_val) max_val = val;
            sum += val;
        }
        printf("\n");

        double avg_val = (double)sum / count;

        char response[1024];
        sprintf(response, 
            "Results:\n -> Min: %d\n -> Max: %d\n -> Avg: %.2f", 
            min_val, max_val, avg_val);

        send(client_socket, response, strlen(response), 0);

        delete[] numbers;
    }

    closesocket(client_socket);
    printf("Client %s disconnected.\n\n", str_addr);
}

void server()
{
    short port = DEFAULT_PORT;
    struct sockaddr_in saddr = {};

    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = htonl(INADDR_ANY);
    saddr.sin_port = htons(port);

    SOCKET s = socket(AF_INET, SOCK_STREAM, 0);
    if (s == INVALID_SOCKET) {
        error_msg("Can't create socket");
        return;
    }

    if (bind(s, (sockaddr *)&saddr, sizeof(saddr)) != 0) {
        error_msg("Can't bind socket to address");
        return;
    }

    if (listen(s, CONNECTION_QUEUE) != 0) {
        error_msg("Can't listen for connections");
        return;
    }

    printf("Server started on port %d. Waiting for arrays...\n", port);

    while (1)
    {
        sockaddr_in client_addr = {};
        int len = sizeof(client_addr);
        SOCKET new_socket = accept(s, (sockaddr *)&client_addr, &len);

        if (new_socket == INVALID_SOCKET) {
            error_msg("Can't accept connection");
            continue;
        }

        handle_connection(new_socket, &client_addr);
    }

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