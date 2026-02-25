#ifndef OWN_PROTOTCOL_H
#define OWN_PROTOCOL_H

#define MAX_ARRAY_SIZE 256

struct ClientRequest
{
    int count;
    int data[MAX_ARRAY_SIZE];
};

struct ServerResponse
{
    int max, min;
    double average;
};

#endif