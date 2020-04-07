#include <iostream>
#include <cstdio>
#include <cstdlib>
using namespace std;

int main() {
        int n;
        cin >> n;
        cout << n << endl;
        srand(time(NULL) * n + n - 47);
        for(int i = 0; i < n; i++) {
                cout << (rand()%n) << endl;
        }
}
