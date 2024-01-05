#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "chat.h"
#include <unistd.h>
#include <ctype.h>
#include <fcntl.h>
#include <ncurses.h>
#include <pthread.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ncurses.h>


CLIENT *cl;
pthread_mutex_t logMutex = PTHREAD_MUTEX_INITIALIZER;

void* updateThread(void* arg) {
    
    int size= 100;
    char message [size];
    char * total_message;
    char * author = (char*)arg;
    int  *return_error;

    while (1) {
        mvprintw(50, 1, "%s: ", author);
        refresh();
        getnstr(message, size-1);
        refresh();

        
        asprintf(&total_message, "%s: %s", author, message); 
        pthread_mutex_lock(&logMutex);
        return_error = write_1(&total_message, cl);
        if (return_error == (int *) NULL) clnt_perror (cl, "error");
        mvprintw(50, 1, "%s:                                                                 ", author);
        pthread_mutex_unlock(&logMutex);
        free(total_message);
    
    }

    return NULL;
}

void* logThread(void* arg) {
    char *read_1_arg;

    while (1) {
        
        sleep(1);
        

        pthread_mutex_lock(&logMutex);
        int y, x;
        getyx(stdscr, y, x);
        printf("%d", y);
            
        char ** chat = getchat_1((void*)&read_1_arg, cl);
        
        mvprintw(1,0, "%s", *chat);
        refresh();
        
        move(50,x);

        pthread_mutex_unlock(&logMutex);
    }
    return NULL;
}


int main(int argc, char **argv) {

    if (argc < 3){
        exit(1);
    }
    char *server = argv[1];
    char* author = argv[2];

    cl = clnt_create(server, CHATPROGRAM, CHATVERSION, "tcp");
    if (cl == NULL) {
        clnt_pcreateerror(server);
        exit(1);
    }
    
    initscr();
    mvprintw(0,0, ".......Welcome %s\n", author);
    refresh();

    pthread_t logThreadId;
    pthread_t updateThreadId;

    if (pthread_create(&logThreadId, NULL, logThread, NULL) != 0) exit(1);

    if (pthread_create(&updateThreadId, NULL, updateThread, author) != 0) exit(1);

    pthread_join(logThreadId, NULL);
    pthread_join(updateThreadId, NULL);
        
    
    return 0;
}