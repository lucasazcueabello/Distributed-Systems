#include "chat.h"
#include <string.h>
int *
write_1_svc(char **argp, struct svc_req *rqstp)
{
	static int msg;

    FILE *file;
    file = fopen("log.txt", "a+");
    if (file == NULL) exit(1);
    fprintf(file, "%s\n", *argp);
    fclose(file);

	return &msg;
}

char **
getchat_1_svc(void *argp, struct svc_req *rqstp)
{
	static char * chat_history;

    FILE *file;
    file = fopen("log.txt", "r+");
    if (file == NULL) exit(1);
    chat_history = malloc(sizeof(char));
    int pos = 0;
    char buf;
    while (buf != EOF){
        buf = fgetc(file);
        if (buf != EOF){
            chat_history = realloc(chat_history, strlen(chat_history)+1);
            chat_history[pos] = buf;
            pos++;
        }

    }
	return &chat_history;
}


