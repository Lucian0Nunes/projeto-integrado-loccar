// <![CDATA[
/* 
 *****************************************************************************
                     INSTRU��ES DE UTILIZA��O
 ****************************************************************************** 

Para utilizar esse esquema de m�scara voc� deve importar um conjunto de arquivos com c�digo
JavaScript. Considerando que todos eles est�o no diret�rio "[public_html]/javascript" o c�digo
para import dos arquivos no ADF ser� o seguinte:
    <!-- ******************** -->
    <af:resource type="javascript" source="/javascript/mootools-core-1.3-full-nocompat.js"/>
    <af:resource type="javascript" source="/javascript/mootools-more-1.3.0.1.js"/>
    <af:resource type="javascript" source="/javascript/Meio.Mask.js"/>
    <af:resource type="javascript" source="/javascript/Meio.Mask.Fixed.js"/>
    <af:resource type="javascript" source="/javascript/Meio.Mask.Extras.js"/>
    <af:resource type="javascript" source="/javascript/Meio.Mask.Reverse.js"/>
    <af:resource type="javascript" source="/javascript/Meio.Mask.Repeat.js"/>
    <af:resource type="javascript" source="/javascript/Meio.Mask.Regexp.js"/>
    <!-- ==== -->
    <af:resource type="javascript" source="/javascript/applyMasks.js"/>
    <!-- ******************** -->

� importante que o c�digo mostrado acima esteja posicionado ap�s qualquer outra refer�ncia
a c�digo JavaScript existente na p�gina.

Para aplicar as m�scaras no onLoad da janela use da seguinte forma:

    <af:resource type="javascript">
        // <![CDATA[
        window.addEvent('load', function() {
            $$('input[name^=cnpj]').each(function(input){
                input.meiomask('fixed.cnpj');
            });
        });
        // ]]>
    </af:resource>

Esse c�digo n�o funciona em telas baseadas em abas e em tabelas onde s�o 
inseridas novas linhas. Para esses casos utilize a fun��o "applyMask(event)".

No exemplo acima, a m�scara ser� aplicada a todos os campos "input"
cujo o valor do atributo "name" come�a com o termo "cnpj".

As m�scaras dispon�veis s�o:
    Fixed
        fixed.phone
        fixed.phone-us
        fixed.cpf
        fixed.cnpj
        fixed.date
        fixed.date-us
        fixed.cep
        fixed.time
        fixed.cc
    Reverse
        reverse.integer
        reverse.decimal
        reverse.decimal-us
        reverse.reais
        reverse.dollar
    Regexp
        regexp.ip
        regexp.email
    Repeat
        repeat

Os operadores l�gias para sele��o dos campos pelo nome s�o as seguintes:
    '='  : � igual a
    '*=' : cont�m
    '^=' : come�a com
    '$=' : termina com
    '!=' : n�o � igual a
    '~=' : contido numa lista separada por espa�o-em-branco
    '|=' : contido numa lista separada por '-' (h�fem)

Aplica��o de demonstra��o de funcionamento do MeioMask: http://jsfiddle.net/fabiomcosta/8TFvp/252/

Documenta��o de "selectors" do MooTools: http://mootools.net/docs/core125/core/Utilities/Selectors

*/

/* *****************************************************************************
                    FUN��O PARA APLICAR M�SCARA NO ADF
 ****************************************************************************** */

/**
 * Para aplicar a m�scara em um campo texto do formul�rio
 * utilizando um <af:clientListener/> e um <af:clientAttribute/>.
 * A configura��o do <af:clientListener/> deve ser a seguinte:
 *   - type: focus
 *   - method: applyMask
 * A configura��o do <af:clientAttribute/> deve ser a seguinte:
 *   - name: meioMask
 *   - value: meio_mask_name
 * O "meio_mask_name" deve ser substitu�do por um dos tipos de m�scaras definidos
 * no in�cio do arquivo texto
 * Eis o exemplo da configura��o de um <af:inputText/> para aplicar a m�scara:
 *
 *    <af:inputText [... atributos ...] id="cnpjEmpresa">
 *        <af:clientListener type="focus" method="applyMask"/>
 *        <af:clientAttribute name="meioMask" value="fixed.cnpj"/>
 *    </af:inputText>
 *
 * @param event
 */

function applyMask(event) {
    try {
        var htmlId;
        var obj;
        var mask;

        htmlId = event.getSource().getClientId();
        mask = event.getSource().getProperty("meioMask");

        if (htmlId.indexOf("::content") <= 0) {
            htmlId = htmlId + "::content";
        }
        //end if
        obj = document.getElementById(htmlId);

        if (obj) {
            $$(obj).meiomask(mask);
        }
        //end if
    }
    catch (_exception) {
        // alert(_exception.msg);
    }
    //end try ... catch
}
//end function
/* *****************************************************************************
 * O c�digo abaixo � uma alternativa � utiliza��o do MooTools e do MeioMask.
 * A desvantagem � que devem ser aplicados no evento "keyPress" dos campos de 
 * entrada de texto.
 * 
 * Existe apenas a fun��o para aplicar a m�scada do CNPJ pronto para utiliza��o 
 * com o ADF que serve como exemplo para a elabora��o de m�todos para as demais 
 * m�scaras que forem necess�rias.
 * 
 * Coloquei as duas alternativas juntas para diminuir a quantidade de arquivos.
 ****************************************************************************** */

/**
 * Aplica a m�scara de CNPJ em um campo texto do formul�rio
 * utilizando um <af:clientListener/>.
 * A configura��o do <af:clientListener/> deve ser a seguinte:
 *   - type: keyPress
 *   - method: maskCnpj
 * @param event
 */
function maskCnpj(event) {
    try {
        var clientId;
        var obj;

        clientId = event.getSource().getClientId();

        if (clientId.indexOf("::content") <= 0) {
            clientId = clientId + "::content";
        }

        obj = document.getElementById(clientId);
        if (obj.maxLenght != 18) {
            obj.maxLenght = 18;
        }
        mascara(obj, cnpj);

    }
    catch (_exception) {
        alert(_exception.msg);
    }
}

/**
 * mascara(o,f)
 * @param o
 * @param f
 */
function mascara(o, f) {
    v_obj = o
    v_fun = f
    setTimeout("execmascara()", 1)
}

/**
 * execmascara()
 */
function execmascara() {
    v_obj.value = v_fun(v_obj.value)
    //v_obj.val(v_fun(v_obj.val()))
}

/**
 * leech(v)
 * @param v
 */
function leech(v) {
    v = v.replace(/o/gi, "0")
    v = v.replace(/i/gi, "1")
    v = v.replace(/z/gi, "2")
    v = v.replace(/e/gi, "3")
    v = v.replace(/a/gi, "4")
    v = v.replace(/s/gi, "5")
    v = v.replace(/t/gi, "7")
    return v
}

/**
 * soNumeros(v)
 * @param v
 */
function soNumeros(v) {
    return v.replace(/\D/g, "")
}

/**
 * telefone(v)
 * @param v
 */
function telefone(v) {
    v = v.replace(/\D/g, "")//Remove tudo o que n�o � d�gito
    v = v.replace(/^(\d\d)(\d)/g, "($1) $2")//Coloca par�nteses em volta dos dois primeiros d�gitos
    v = v.replace(/(\d{4})(\d)/, "$1-$2")//Coloca h�fen entre o quarto e o quinto d�gitos
    return v
}

/**
 * cpf(v)
 * @param v
 */
function cpf(v) {
    v = v.replace(/\D/g, "")//Remove tudo o que n�o � d�gito
    v = v.replace(/(\d{3})(\d)/, "$1.$2")//Coloca um ponto entre o terceiro e o quarto d�gitos
    v = v.replace(/(\d{3})(\d)/, "$1.$2")//Coloca um ponto entre o terceiro e o quarto d�gitos
    //de novo (para o segundo bloco de n�meros)
    v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2")//Coloca um h�fen entre o terceiro e o quarto d�gitos
    return v
}

/**
 * cep(v)
 * @param v
 */
function cep(v) {
    v = v.replace(/D/g, "")//Remove tudo o que n�o � d�gito
    v = v.replace(/^(\d{5})(\d)/, "$1-$2")//Esse � t�o f�cil que n�o merece explica��es
    return v
}

/**
 * cnpj(v)
 * @param v
 */
function cnpj(v) {
    v = v.replace(/\D/g, "")//Remove tudo o que n�o � d�gito
    v = v.replace(/^(\d{2})(\d)/, "$1.$2")//Coloca ponto entre o segundo e o terceiro d�gitos
    v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3")//Coloca ponto entre o quinto e o sexto d�gitos
    v = v.replace(/\.(\d{3})(\d)/, ".$1/$2")//Coloca uma barra entre o oitavo e o nono d�gitos
    v = v.replace(/(\d{4})(\d)/, "$1-$2")//Coloca um h�fen depois do bloco de quatro d�gitos
    return v
}

/**
 * romanos(v)
 * @param v
 */
function romanos(v) {
    v = v.toUpperCase()//Mai�sculas
    v = v.replace(/[^IVXLCDM]/g, "")//Remove tudo o que n�o for I, V, X, L, C, D ou M
    //Essa � complicada! Copiei daqui: http://www.diveintopython.org/refactoring/refactoring.html
    while (v.replace(/^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$/, "") != "")
        v = v.replace(/.$/, "")
    return v
}

/**
 * site(v)
 * @param v
 */
function site(v) {
    //Esse sem comentarios para que voc� entenda sozinho ;-)
    v = v.replace(/^http:\/\/?/, "")
    dominio = v
    caminho = ""
    if (v.indexOf("/") >  - 1)
        dominio = v.split("/")[0]
    caminho = v.replace(/[^\/]*/, "")
    dominio = dominio.replace(/[^\w\.\+-:@]/g, "")
    caminho = caminho.replace(/[^\w\d\+-@:\?&=%\(\)\.]/g, "")
    caminho = caminho.replace(/([\?&])=/, "$1")
    if (caminho != "")
        dominio = dominio.replace(/\.+$/, "")
    v = "http://" + dominio + caminho
    return v
}


function maskPlaca(event) {
    try {
        var clientId;
        var obj;

        clientId = event.getSource().getClientId();

        if (clientId.indexOf("::content") <= 0) {
            clientId = clientId + "::content";
        }

        obj = document.getElementById(clientId);
        if (obj.maxLenght != 8) {
            obj.maxLenght = 8;
        }
        mascara(obj, placa);

    }
    catch (_exception) {
        alert(_exception.msg);
    }
}

function placa(v) {
                v = v.replace(/-/g, "");
                var v1 = v.substring(0, 3);
                var v2 = v.substring(3, 7);
                v1 = v1.replace(/\d|\W/g, "");
                v1 = v1.toUpperCase();
                v2 = v2.replace(/\D/g, "");
                v = v1;
                if (v2 != "")
                        v += "-" + v2;
        return v;
}

// ]]>