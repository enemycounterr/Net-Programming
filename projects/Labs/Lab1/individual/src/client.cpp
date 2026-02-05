
#include "helper.h"
#include <vector>

#pragma warning(disable : 4996)
// #pragma comment(lib, "ws2_32.lib")

#define DEFAULT_PORT 5553

void error_msg(const char *msg)
{
    printf("[ERROR]: %s\n", msg);
}

void client()
{
    // Якщо сервер на цьому ж ПК - 127.0.0.1
    // Якщо на іншому - зміни IP
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
    printf("Enter '0' as array size to exit.\n\n");

    while (1)
    {
        int count = 0;
        printf("Enter count of numbers: ");
        if (scanf("%d", &count) != 1)
        { // Чистимо буфер, якщо ввели букви
            while (getchar() != '\n')
                ;
            continue;
        }

        if (count <= 0)
        {
            printf("Exiting...\n");
            break;
        }

        // Створюємо масив
        std::vector<int> numbers(count);
        printf("Enter %d numbers: ", count);
        for (int i = 0; i < count; i++)
        {
            scanf("%d", &numbers[i]);
        }
        printf("The whole data was stored into array");

        // 1. ВІДПРАВКА РОЗМІРУ (Кількості)
        // (char*)&count - беремо адресу змінної і кажемо "вважай це просто байтами"
        int ret = send(s, (char *)&count, sizeof(int), 0);
        if (ret <= 0)
        {
            error_msg("Can't send size");
            break;
        }

        // 2. ВІДПРАВКА ДАНИХ (Масиву)
        // numbers.data() повертає вказівник на перший елемент масиву (int*)
        // sizeof(int) * count - це загальна вага посилки в байтах
        ret = send(s, (char *)numbers.data(), sizeof(int) * count, 0);
        if (ret <= 0)
        {
            error_msg("Can't send array data");
            break;
        }

        printf("Sent array to server. Waiting for result...\n");

        // 3. ОТРИМАННЯ ВІДПОВІДІ
        char response[1024] = {0};
        ret = recv(s, response, sizeof(response), 0);

        if (ret <= 0)
        {
            error_msg("Server disconnected");
            break;
        }

        // Ставимо нуль-термінатор про всяк випадок
        response[ret] = '\0';
        printf("\nServer Reply:\n%s\n-----------------------\n", response);
    }

    closesocket(s);
}

int main()
{
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
    client();
    WSACleanup();
    return 0;
}