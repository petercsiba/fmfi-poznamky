#include <iostream>
#include <vector>
#include <cstdio> 
#include <algorithm>
#include <set>
#include <string>
#include <map>
#include <queue>
#include <iomanip>
#include <cmath>
#include <fstream> 
#include <sys/time.h>
#include <ctime> 

using namespace std;

#define REP(i, to) for(int i=0; i<to; i++)
#define INT(i) ((int) i)

typedef pair<string, int> PSI; 
typedef unsigned long long int uLLI; 

#define MAX_WORDS 20000
#define PRINT_K 250 

#define BINARY_TREE 0
#define SPLAY_TREE 1 
#define STL_TREE 2 
string dict_names[] = {"BSTree", "SplayTree", "StlTree"}; 

string INPUTS[] = {"in/1_alice.txt", "in/2_darwin.txt"};

#define INPUTS_SIZE 2 

#define LESS -1
#define EQUAL 0 
#define MORE 1
int comparison_count = 0; 
int compare_counting_int(string& a, string& b){
  comparison_count += 1; 
  
  if(a.size() == b.size() && a == b) {
    return EQUAL; 
  }
  return (a < b) ? LESS : MORE; 
}
class compare_counting_bool {
  public: 
    bool operator() (const string& lhs, const string& rhs) const{
        comparison_count += 1; 
        return lhs < rhs;
    }
};

uLLI get_time() {
    struct timeval tv;
    gettimeofday(&tv,NULL);
    return tv.tv_sec*(uLLI)1000000+tv.tv_usec;
}

class CountingDictionary{
  public: 
    virtual void init() = 0; 
    virtual void add(string& val) = 0; 
    virtual void frequent(vector<PSI>& coll, int k) = 0; 
    virtual void print() = 0; 
    
    CountingDictionary() {} 
    virtual ~CountingDictionary() {}
};

class CountingTreeNode : public CountingDictionary{
   public: 
    bool is_empty() {return value == NULL;} 
    
    CountingTreeNode() {
      this->init(); 
    }
    //virtual 
    ~CountingTreeNode() {
      if(value != NULL) delete value; 
      if(left != NULL) delete left; 
      if(right != NULL) delete right; 
    }
    
    //virtual
    void init(){
      this->value = NULL; 
      this->left = NULL; 
      this->right = NULL;
      this->parent = NULL;
      this->count = 0; 
    }
    
    //virtual
    void frequent(vector<PSI>& coll, int k){
      if(this->is_empty()) return; 
    
      if(this->count >= k) coll.push_back(PSI(*(this->value), this->count)); 
      if(this->left != NULL) this->left->frequent(coll, k); 
      if(this->right != NULL) this->right->frequent(coll, k); 
    }
    
    void print(){
      this->print_depth(0); 
    }
    
    CountingTreeNode* root(){
      return (this->parent == NULL) ? this : this->parent->root(); 
    }
    
  protected:
    string* value; 
    int count;     
    
    CountingTreeNode* left; 
    CountingTreeNode* right; 
    CountingTreeNode* parent; 
    
    virtual CountingTreeNode* get_instance() = 0; 
     
    CountingTreeNode* search_and_add(string& val, CountingTreeNode* p){
      if(this->is_empty()){
        this->value = new string(val); 
        this->count += 1; 
        this->left = get_instance(); 
        this->right = get_instance(); 
        this->parent = p; 
        return this; 
      }
      else{
        int cmp_res = compare_counting_int(val, *(this->value)); 
        if(cmp_res == EQUAL) {
          this->count += 1;
          return this; 
        }
        if(cmp_res == LESS) {
          return this->left->search_and_add(val, this); 
        }
        if(cmp_res == MORE) {
          return this->right->search_and_add(val, this); 
        }
      }
      
      return NULL; //NOTE: should not happen 
    }
    
    void print_depth(int d){
      cout << string(2*d, ' ') << ((value==NULL)?"NULL":*value) << ":" << count << endl;
      if(this->left != NULL) this->left->print_depth(d + 1); 
      if(this->right != NULL) this->right->print_depth(d + 1); 
    }
}; 

class BinarySearchTree : public CountingTreeNode{
  public: 
    BinarySearchTree() : CountingTreeNode() {} 
    //virtual 
    ~BinarySearchTree() {} 
    
    //virtual
    void add(string& val) {
      this->search_and_add(val, this->parent); 
    }
    
  protected: 
    //virtual
    CountingTreeNode* get_instance() {return new BinarySearchTree();} 
};

class SplayNode : public CountingTreeNode{
  public: 
    SplayNode() : CountingTreeNode() {} 
    //virtual 
    ~SplayNode() {} 
  
    //virtual
    void add(string& val) {
      //cout << val << endl; //debug
      SplayNode* node = (SplayNode*) this->search_and_add(val, (CountingTreeNode*) this->parent); 
      splay(node); 
    }
    
  protected: 
    //virtual
    CountingTreeNode* get_instance() {return new SplayNode();} 
    
    static SplayNode* p(SplayNode* x) {
      return (x == NULL) ? NULL : (SplayNode*) x->parent; 
    }
    
    static SplayNode* l(SplayNode* x) {
      return (x == NULL) ? NULL : (SplayNode*) x->left; 
    }
    
    static SplayNode* r(SplayNode* x) {
      return (x == NULL) ? NULL : (SplayNode*) x->right; 
    }
    
    static SplayNode* g(SplayNode* x) {
      return p(p(x)); 
    }
    
    static void atomic(CountingTreeNode* &a_old, SplayNode* a_new, CountingTreeNode* &b_old, SplayNode* b_new){
      a_old = a_new; 
      b_old = b_new; 
    }
  
    static void rotate_left(SplayNode* y){
      SplayNode* x = r(y); 
      SplayNode* z = p(y); 
      
      if(z != NULL){
        if(l(z) == y) z->left = x; 
        if(r(z) == y) z->right = x; 
      }
      
      atomic(x->left, y, y->right, l(x)); 
      atomic(x->parent, z, y->parent, x); 
        
      if(r(y) != NULL) r(y)->parent = y; 
    }
    
    static void rotate_right(SplayNode* y){
      SplayNode* x = l(y); 
      SplayNode* z = p(y); 
      
      if(z != NULL){
        if(r(z) == y) z->right = x; 
        if(l(z) == y) z->left = x; 
      }
      
      atomic(x->right, y, y->left, r(x)); 
      atomic(x->parent, z, y->parent, x); 
        
      if(l(y) != NULL) l(y)->parent = y; 
    }
    
    static void splay(SplayNode* x){
      do{
        if(x == l(p(x))){
          if(g(x) == NULL) rotate_right(p(x)); 
          else if(p(x) == l(g(x))) {rotate_right(g(x)); rotate_right(p(x));}
          else if(p(x) == r(g(x))) {rotate_right(p(x)); rotate_left(p(x));} 
        }
        else if(x == r(p(x))){
          if(g(x) == NULL) rotate_left(p(x)); 
          else if(p(x) == r(g(x))) {rotate_left(g(x)); rotate_left(p(x));} 
          else if(p(x) == l(g(x))) {rotate_left(p(x)); rotate_right(p(x));} 
        }
      }while(p(x) != NULL); 
    }
};

class SplayTree : public CountingDictionary {
  public:
    void init(){
      this->root = new SplayNode(); 
    }
    
    void add(string& val) {
      this->root->add(val); 
      this->root = (SplayNode*) this->root->root(); 
    }
    void frequent(vector<PSI>& coll, int k) {
      this->root->frequent(coll, k); 
    }
    void print(){
      this->root->print(); 
    }
        
    SplayTree() {this->init();} 
    ~SplayTree() {delete this->root;}
    
  protected: 
    SplayNode* root; 
};


void test_splay_tree(){
  SplayTree* tree = new SplayTree(); 
  string to_add[] = {"ZZ", "BB", "CC", "AA", "AB", "BA", "AA", "AB", "AA", "AA", "AB", "ZZ"};
  REP(i, 12){
    cout << "==Adding " << to_add[i] << endl; 
    tree->add(to_add[i]); 
    tree->print(); 
  }
}

class STLTree : public CountingDictionary{
  public: 
    void init(){}
    
    void add(string& val) {
      this->dict[val]++; 
    }
    void frequent(vector<PSI>& coll, int k) {
      for(map<string, int>::iterator iter = dict.begin(); iter != dict.end() ; iter++){
        if(iter->second >= k){
          coll.push_back(PSI(iter->first, iter->second)); 
        }
      }
    }
    void print(){
      for(map<string, int>::iterator iter = dict.begin(); iter != dict.end() ; iter++){
        cout << iter->first << "\t" << iter->second << endl;
      }
    }
        
    STLTree() {} 
    ~STLTree() {}
    
  protected: 
    map<string, int, compare_counting_bool> dict; 
};

void experiment_B(){

  ofstream freq_top("out/freq_top.txt");
  ofstream fstats("out/stats.txt");

  REP(in_i, INPUTS_SIZE){
    string filename = INPUTS[in_i]; 
    ifstream filestream(filename.c_str()); 
    
    vector<CountingDictionary*> dictionaries;
    dictionaries.push_back(new BinarySearchTree());
    dictionaries.push_back(new SplayTree());
    dictionaries.push_back(new STLTree());
    
    string line = ""; 
    string word = ""; 
    vector<string> words; 
  
    while(getline(filestream, line)){
      line += "\n"; 
      REP(i, INT(line.size())) {
        char c = line[i];
        if('a' <= c && c <= 'z') word += c; 
        else if('A' <= c && c <= 'Z') word += (char)(c + ('a' - 'A')); 
        else if(word.size() > 0u){
          words.push_back(word); 
          word = ""; 
        }
        
        if(words.size() >= MAX_WORDS) {
          break; 
        }
      }
    }
    
    REP(dict_i, INT(dictionaries.size())){
      cout << "Running " << dict_names[dict_i] << " on " << filename << endl;
    
      comparison_count = 0; 
      uLLI start_time = get_time(); 
       
      CountingDictionary* dict = dictionaries[dict_i]; 
      REP(w_i, INT(words.size())) dict->add(words[w_i]); 
      
      uLLI end_time = get_time(); 
      fstats << "file=" << filename << "\tdict=" << dict_names[dict_i] << "\ttime=" << (end_time - start_time) << "\tcomparison_count=" << comparison_count << endl;
 
      cout << "  Writing out most " <<  PRINT_K << " frequent words" << endl;
      vector<PSI> TF; 
      dict->frequent(TF, PRINT_K); 
      freq_top << "=================file=" << filename << "\t" << "structure=" << dict_names[dict_i] <<  endl; 
      freq_top << "  count=" << TF.size() << endl;
      REP(f_i, INT(TF.size())) freq_top << TF[f_i].first << " " << TF[f_i].second << endl; 
      //if(dict_i == 1) dict->print(); //debug
      
      string filename_freq = "out/freq_" + dict_names[dict_i] + "_" + ((char)(in_i + '0')) + ".txt"; 
      vector<PSI> F;
      dict->frequent(F, 1);
      ofstream ffreq(filename_freq.c_str());
      cout << "  Writing out word frequencies to file=" << filename_freq << endl;
      cout << "  count=" << F.size() << endl; 
      REP(f_i, INT(F.size())) ffreq << F[f_i].first << " " << F[f_i].second << endl; 

      cout << "  Deleting" << endl;      
      delete dict; 
      cout << "  DONE" << endl;
    }   
  }
  
  fstats.close(); 
  
  ifstream in_fstats("out/stats.txt"); 
  string s; 
  while(getline(in_fstats, s)) cout << s << endl;
}

string random_string(int l, int s){
  string result; 
  REP(i, l) result += (char)('a' + (rand() % s)); 
  return result; 
}

pair<int, int> avg_std(vector<int> V){
  long long int s = 0; 
  REP(i, INT(V.size())) s+=V[i]; 
  int avg = s / INT(V.size()); 
  
  s=0; 
  REP(i, INT(V.size())) s += (V[i] - avg) * (V[i] - avg);
  int std = ((int) sqrt((double)s)) / INT(V.size()); 
  
  return pair<int, int>(avg, std); 
}

void experiment_C(){
  const int dictsize = 3; 

  vector<int> try_L; 
  for(int i=2; i<=12; i++) try_L.push_back(i); 
  const int Lsize = try_L.size(); 
  
  vector<int> try_sigma; 
  for(int i=2; i<=26; i+=2) try_sigma.push_back(i); 
  const int sigmasize = try_sigma.size(); 
  
  int z = 20000; 
  int runs = 10; 
  vector<int> times[dictsize][Lsize][sigmasize]; 
  vector<int> comparisons[dictsize][Lsize][sigmasize]; 

  int total_cases = Lsize * sigmasize; 
  int cc = 1; 
  
  REP(L_i, Lsize) REP(sigma_i, sigmasize) {
    cout << "Running\tL=" << try_L[L_i] << "\tsigma=" << try_sigma[sigma_i] << endl;
    cout << "Case " << cc++ << "/" << total_cases << endl;
    
    REP(r_i, runs) {
      vector<string> words; 
      REP(w_i, z) words.push_back(random_string(try_L[L_i], try_sigma[sigma_i])); 
      
      CountingDictionary* dicts[] = {new BinarySearchTree(), new SplayTree(), new STLTree()};
      REP(d_i, dictsize) {
        comparison_count = 0; 
        uLLI start_time = get_time(); 
        
        REP(w_i, INT(words.size())) dicts[d_i]->add(words[w_i]); 
        
        times[d_i][L_i][sigma_i].push_back(get_time() - start_time); 
        comparisons[d_i][L_i][sigma_i].push_back(comparison_count); 
        
        delete dicts[d_i]; 
      }
    }
  }
  
  REP(d_i, dictsize) {
    cout << "Results for " << dict_names[d_i] << endl;
  
    ofstream frand(string("out/rand_" + dict_names[d_i] + ".dat").c_str()); 
    frand << "L\tsigma\tavg(time)\tstd(time)\tavg(cmp)\tstd(cmp)" << endl;  
    
    REP(L_i, Lsize) REP(sigma_i, sigmasize) {  
      pair<int, int> ptimes = avg_std(times[d_i][L_i][sigma_i]);
      pair<int, int> pcomparisons = avg_std(comparisons[d_i][L_i][sigma_i]);
      
      frand << try_L[L_i] << "\t" << try_sigma[sigma_i] << "\t" << ptimes.first << "\t" << ptimes.second << "\t" << pcomparisons.first << "\t" << pcomparisons.second << endl;
    }
    
    frand.close(); 
  }
}

int main(){
  srand(time(NULL)); 

  //test_splay_tree(); 
  //experiment_B(); 
  experiment_C(); 
  
  return 0; 
}
