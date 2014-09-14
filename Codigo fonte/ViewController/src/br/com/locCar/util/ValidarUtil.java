package br.com.locCar.util;

import java.math.BigInteger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;


public abstract class ValidarUtil {
    
    //Essa classe só poderá conter métodos de validação que não necessitem
    //de um iterator para buscar informações no banco, Ex: verificar se um CPF já consta na base. 
    //
    public ValidarUtil() {
        super();
    }
    
    public void validarEmail(FacesContext facesContext,
                             UIComponent uIComponent, Object object) {
        String email = (String)object;
        if (object != null) {
            if (!EmailValidator.INSTANCE.validate(email)) {
                FacesMessage msg =
                    new FacesMessage("Email inv\u00E1lido", "Informe um email \nv\u00E1lido");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }
    
    public void validarTelefone(FacesContext facesContext,
                               UIComponent uIComponent, Object object) {       
        
        String opcao = (String)object;
        opcao = opcao.replaceAll("[^0-9]", "");
        if(opcao.length()<10){
            FacesMessage msg =
                new FacesMessage("Telefone inv\u00E1lido", "Informe um telefone v\u00E1lido");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);            
            }       
    }
    
    public void validarNome(FacesContext facesContext,
                               UIComponent uIComponent, Object object) {       
        
        String opcao = (String)object;
        opcao = opcao.replaceAll(" ", "");
        if(opcao.length()<3){
            FacesMessage msg =
                new FacesMessage("Menor que 3 caracteres", "Informe o nome completo!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
            
            }
       
    }

    public static boolean validaCnpj(String cnpj) {

        int soma = 0;

        char[] aCnpj = cnpj.toCharArray();

        soma += (parseCharToInt(aCnpj[0]) * 5);
        soma += (parseCharToInt(aCnpj[1]) * 4);
        soma += (parseCharToInt(aCnpj[2]) * 3);
        soma += (parseCharToInt(aCnpj[3]) * 2);
        soma += (parseCharToInt(aCnpj[4]) * 9);
        soma += (parseCharToInt(aCnpj[5]) * 8);
        soma += (parseCharToInt(aCnpj[6]) * 7);
        soma += (parseCharToInt(aCnpj[7]) * 6);
        soma += (parseCharToInt(aCnpj[8]) * 5);
        soma += (parseCharToInt(aCnpj[9]) * 4);
        soma += (parseCharToInt(aCnpj[10]) * 3);
        soma += (parseCharToInt(aCnpj[11]) * 2);

        int d1 = soma % 11;
        d1 = d1 < 2 ? 0 : 11 - d1;

        soma = 0;
        soma += (parseCharToInt(aCnpj[0]) * 6);
        soma += (parseCharToInt(aCnpj[1]) * 5);
        soma += (parseCharToInt(aCnpj[2]) * 4);
        soma += (parseCharToInt(aCnpj[3]) * 3);
        soma += (parseCharToInt(aCnpj[4]) * 2);
        soma += (parseCharToInt(aCnpj[5]) * 9);
        soma += (parseCharToInt(aCnpj[6]) * 8);
        soma += (parseCharToInt(aCnpj[7]) * 7);
        soma += (parseCharToInt(aCnpj[8]) * 6);
        soma += (parseCharToInt(aCnpj[9]) * 5);
        soma += (parseCharToInt(aCnpj[10]) * 4);
        soma += (parseCharToInt(aCnpj[11]) * 3);
        soma += (parseCharToInt(aCnpj[12]) * 2);


        int d2 = soma % 11;
        d2 = d2 < 2 ? 0 : 11 - d2;

        if (parseCharToInt(aCnpj[12]) == d1 &&
            parseCharToInt(aCnpj[13]) == d2) {
            return true;
        } else {
            return false;
        }
    }

    private static int parseCharToInt(char c) {
        return Integer.parseInt(Character.toString(c));
    }

    public boolean validaCPF(String cpf) {

        int d1, d2;
        String strCpf;
        int digito1, digito2, resto;
        int digitoCPF;
        String nDigResult;
        boolean resultado;

        strCpf = cpf;
        resultado = false;

        for (int i = 0; i < 10; i++) {
            if (Long.parseLong(cpf.replaceAll(String.valueOf(i), "0")) == 0L) {
                resultado = false;
                break;
            } else {
                resultado = true;
            }
        }

        if (resultado) {
            d1 = d2 = 0;
            digito1 = digito2 = resto = 0;

            for (int nCount = 1; nCount < strCpf.length() - 1; nCount++) {
                digitoCPF =
                        Integer.valueOf(strCpf.substring(nCount - 1, nCount)).intValue();

                //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
                d1 = d1 + (11 - nCount) * digitoCPF;

                //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
                d2 = d2 + (12 - nCount) * digitoCPF;
            }

            //Primeiro resto da divisão por 11.
            resto = (d1 % 11);

            //Se o resultado for 0 ou 1 o digito � 0 caso contr�rio o digito � 11 menos o resultado anterior.
            if (resto < 2) {
                digito1 = 0;
            } else {
                digito1 = 11 - resto;
            }

            d2 += 2 * digito1;

            //Segundo resto da divis�o por 11.
            resto = (d2 % 11);

            //Se o resultado for 0 ou 1 o digito � 0 caso contr�rio o digito � 11 menos o resultado anterior.
            if (resto < 2) {
                digito2 = 0;
            } else {
                digito2 = 11 - resto;
            }

            //Digito verificador do CPF que est� sendo validado.
            String nDigVerific =
                strCpf.substring(strCpf.length() - 2, strCpf.length());

            //Concatenando o primeiro resto com o segundo.
            nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

            resultado = nDigVerific.equals(nDigResult);
        }
        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
        return resultado;
    }
    
    public String gerarMD5(String senha) throws NoSuchAlgorithmException {  
        MessageDigest md = MessageDigest.getInstance("MD5");  
        BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));  
        String crypto = hash.toString(16);  
        if (crypto.length() %2 != 0)  
            crypto = "0" + crypto;  
        return crypto;  
    }
}
