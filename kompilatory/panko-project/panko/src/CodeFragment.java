public class CodeFragment {
        private String code;
        private String register;

        public CodeFragment() {
                this.code = "";
                this.register = null;
        }
        
        public CodeFragment(String _code, String _register) {
            this.code = _code;
            this.register = _register;
        }

        public void addCode(String code) {
                this.code += code;
        }

        public void addCode(CodeFragment fragment) {
                this.code += fragment.toString();
        }

        public String toString() {
                return this.code;
        }

        public void setRegister(String register) {
                this.register = register;
        }

        public String getRegister() {
                return this.register;
        }
}
