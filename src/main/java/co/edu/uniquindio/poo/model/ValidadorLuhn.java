package co.edu.uniquindio.poo.model;

public class ValidadorLuhn {
    private ValidadorLuhn(){}
    public static boolean validarLuhn(String numero){
        if(numero==null)return false;
        String digitos=numero.replaceAll("\\s","");
        if(!digitos.matches("[0-9]{13,19}"))
            return false;
        int suma=0;
        boolean duplicar=false;
        for(int i=digitos.length()-1;i>=0;i--){
            int n=digitos.charAt(i)-'0';
            if(duplicar){n*=2;
                if(n>9)n-=9;
            }
            suma+=n;duplicar=!duplicar;
        }
        return suma%10==0;
    }
}
