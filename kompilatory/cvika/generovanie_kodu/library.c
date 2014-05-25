#include <stdlib.h>
#include <stdio.h>

int printInt(int a) {
        printf("%d\n", a);
        return 0;
}

int iexp(int a, int b) {
        int ret = 1;
        for (int i = 0; i < b; i++) {
                ret *= a;
        }
        return ret;
}
