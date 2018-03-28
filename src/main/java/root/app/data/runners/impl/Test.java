package root.app.data.runners.impl;

public class Test {
    public static void main(String[] args)  {
        String s1 = "java";
        StringBuilder s2 = new StringBuilder("java");
        replaceString(s1);
        replaceStringBuilder(s2);
        System.out.println(s1 + s2);
    }
    static void replaceString(String s) {
        s = s.replace('j', 'l');
    }
    static void replaceStringBuilder(StringBuilder s) {
        s.append("c");
    }
    }