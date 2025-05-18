/*lexico.l */

%{
    #include "lexico.h"
    #include <stdio.h>
    int contador_errores = 0;     
%}

/* Macros expersiones regulares */
D   [0-9]
L   [a-zA-Z]
ERROR [^a-zA-Z_0-9+\-*/=;() \t\n\r]

/* Opciones de Flex */
%option yylineno

%%

 /* Expresiones regulares */

"print"        { return PRIN; }

{L}+            { return IDEN; }

"="             { return ASIGN; }
"+"             { return SUMA; }
"-"             { return RESTA; }
"/"             { return DIVI; }
"*"             { return MULT; }

{D}+            { return ENTE; }

";"             { return PYCO; }
"("             { return PARI; }
")"             { return PARD; }

[\t\n\r]+            { }        /* ER ignora espacios en blanco */
"//"(.*)\n           { }       /* Comentarios */ 
{ERROR}+               { printf("Error en la linea %d: %s\n", yylineno, yytext); }

%%

int main(int argc, char *argv[]){
    int tok;
    while ((tok = yylex()) !=0){
        printf("Token: %d, %s, %d\n",tok, yytext, yyleng);
    }
}