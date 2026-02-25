#include "../include/own_protocol.h"
#include "common_net.h"

#pragma warning(disable : 4996)

#define DEFAULT_PORT 5553

void error_msg(const char *msg)
{
    printf("[ERROR]: %s\n", msg);
}

void client()
{
    char host[256] = "127.0.0.1";
    short port = DEFAULT_PORT;

    struct sockaddr_in saddr = {};
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = inet_addr(host);
    saddr.sin_port = htons(port);

    SOCKET s = socket(AF_INET, SOCK_STREAM, 0);
    if (s == INVALID_SOCKET)
    {
        error_msg("Can't create socket");
        return;
    }

    if (connect(s, (sockaddr *)&saddr, sizeof(saddr)) != 0)
    {
        error_msg("Can't connect to server");
        return;
    }

    printf("Connected to server!\n");

    while (1)
    {
        ClientRequest req;
        memset(&req, 0, sizeof(ClientRequest));

        printf("\nEnter count of numbers (0 to exit): ");
        if (scanf("%d", &req.count) != 1 || req.count <= 0)
            break;

        if (req.count > MAX_ARRAY_SIZE)
        {
            printf("Error: Max size is %d\n", MAX_ARRAY_SIZE);
            continue;
        }

        printf("Enter %d numbers: ", req.count);
        for (int i = 0; i < req.count; i++)
        {
            scanf("%d", &req.data[i]);
        }

        if (send(s, (char *)&req, sizeof(ClientRequest), 0) <= 0)
            break;

        ServerResponse res;
        if (recv(s, (char *)&res, sizeof(ServerResponse), 0) <= 0)
            break;

        printf("\nServer reply:\n[MIN]:%d\n[MAX]:%d\n[AVG]:%.2f", res.min, res.max, res.average);
    }

    closesocket(s);
}

int main()
{
    common_init_handler();
    client();
    common_exit_handler();
    return 0;
}