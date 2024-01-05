/******************************************************************************

                            Online C Compiler.
                Code, Compile, Run and Debug C program online.
Write your code in this editor and press "Run" button to compile and execute it.

*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>

#define NUM_PROCESSES 10

int variable = 0;

int getCurrentValue(int buf){
    return buf;
}

int updateCurrentValue(int value){
    return value;
}

void rProcess(int socket){
    char buf = 0;
    int value;

    for (int i=0; i<10; i++){

        //Ask master process for variable value
        buf = 'r';
        write(socket, &buf, 1);

        buf = 's';
        while(buf == 's') read(socket, &buf, 1);

        value = getCurrentValue(buf);
        printf("(Child) Value is %d\n", value);

        sleep(5);

    }
    close(socket);
}

void rwProcess(int socket){
    char buf = 0;
    int value;

    for (int i=0; i<10; i++){

        //Ask master process for variable value
        buf = 'w';
        write(socket, &buf, 1);

        buf = 's';
        while(buf == 's') read(socket, &buf, 1);

        value = getCurrentValue(buf);
        printf("(Child) Value is %d\n", value);

        buf = updateCurrentValue(value+1);

        write(socket, &buf, 1);
        buf = 's';
        while(buf == 's') read(socket, &buf, 1);

        printf("(Child) Current value updated\n");

        sleep(5);

    }
    close(socket);
}

void masterProcess(int socket[NUM_PROCESSES][2]) {
    char buf = 0;
    int value;
    char type;

    while(1) {
        for(int i = 0; i < NUM_PROCESSES; i++){
            buf = 's';
            while(buf == 's') read(socket[i][1], &buf, 1);
            printf("(Parent) received value: %c\n", buf);
            type = buf;
            buf = variable;
            write(socket[i][1], &buf, 1);
            if(type == 'w'){
                buf = 's';
                while(buf == 's') read(socket[i][1], &buf, 1);
                variable = buf;
                buf = 2;
                write(socket[i][1], &buf, 1);
            }

        }
    }
}

int main()
{
    int sockets[NUM_PROCESSES][2];
    char buff;

    for(int i = 0; i < NUM_PROCESSES; i++){
        if (socketpair(AF_UNIX, SOCK_STREAM, 0, sockets[i]) == -1) {
            perror("socketpair");
            exit(1);
        }
    }

    for(int i = 0; i < NUM_PROCESSES; i++) {
        pid_t pid = fork();

        if(pid >= 0) {
            if(pid == 0) {
                //Child Process
                if (i % 2 ==0)
                    rwProcess(sockets[i][0]);
                else
                    rProcess(sockets[i][0]);
                break;
            } else {
                //Parent Process
                if (i != NUM_PROCESSES - 1 ) continue;
                //Master Process Behavior
                masterProcess(sockets);
            }
        } else {
            printf("Fork failed.\n");
            return 1;
        }

    }
    return 0;
}
