/* Definição das máscaras */
var CPF_MASK = "###.###.###-##";
var CNPJ_MASK = "##.###.###/####-##";
var DATE_MASK = "##/##/####";
var MES_ANO_MASK = "##/####";
var UNIDADE_MASK = "####-#";
var VALOR_MONETARIO_MASK = "[###.]###,##";
var CEP = "##.###-###";
var TEL = "##-####-####";
var PLACA_MASK = "aaa-####";
var RENAVAM_MASK = "#########";
var VRG_JUROS_MASK = "###,##";

/**  
 * Função Principal 
 * @param w - O elemento que será aplicado (normalmente this).
 * @param e - O evento para capturar a tecla e cancelar o backspace.
 * @param m - A máscara a ser aplicada.
 * @param r - Se a máscara deve ser aplicada da direita para a esquerda. Veja Exemplos.
 * @param a - 
 * @returns null  
 * 
 * Sintaxe: maskIt (w,e,m[,r[,a]]);
 * Retorno: nenhum.
 * Descrição: Aplica uma máscara no campo do tipo texto.
 * Explicação adicional: Função para aplicação de máscara em campos input do tipo texto. Funções com formato pré definidos funcionam melhor (CPF) que formatos variáveis (moeda).
 * 
 * Os argumentos da função são:
 * w: Referência ao elemento. Normalmente é this.
 * e: Evento usado para cancelar o Backspace.
 * m: Máscara a ser aplicada.
 * r: Aplicar a mascara da direita para a esquerda. Opcional [true|false] - Dafault : false.
 * a: Objeto com informações para aplicar após a máscara. Use você precisar aplicar alguma informação sempre no começo ou no fim do valor independente da máscara, como exemplo "R$" em campos do tipo monetário. Sintaxe: {[pre:'value'[,pos:'value']]}.
 * 
 * Exemplos de Uso:
 * 1. CPF:
 * <form name="teste">
 *         <input type="text" name="cpf" onkeyup="maskIt(this,event,'###.###.###-##')" />
 * </form>
 * 
 * 2. Telefone:
 * <form name="teste">
 *         <input type="text" name="fone" onkeyup="maskIt(this,event,'(##)####-####')" />
 * </form>
 * 
 * 3. Dinheiro
 * <form name="teste">
 *         <input type="text" name="dinheiro" onkeyup="maskIt(this,event,'###.###.###,##',true,{pre:'R$'})" />
 * </form>
 * 
 * 3. Graus
 * <form name="teste">
 *        <input type="text" name="graus" onkeyup="maskIt(this,event,'###,#',true,{pre:'',pos:'º'})" />
 * </form> 
 */
function maskIt(w,e,m,r,a) {
       
       // Cancela se o evento for Backspace
       if (!e) var e = window.event;
       if (e.keyCode) code = e.keyCode;
       else if (e.which) code = e.which;
       
       // Variáveis da função
       var txt  = (!r) ? w.value.replace(/[^\d\w]+/gi,'') : w.value.replace(/[^\d\w]+/gi,'').reverse();
       var mask = (!r) ? m : m.reverse();
       var pre  = (a ) ? a.pre : "";
       var pos  = (a ) ? a.pos : "";
       var ret  = "";

       if (code == 9 || code == 8 || txt.length == mask.replace(/[^a#]+/g,'').length) return false;

       // Loop na máscara para aplicar os caracteres
       for (var x=0, y=0, z=mask.length; x < z && y < txt.length;) {
               if (mask.charAt(x) != '#' && mask.charAt(x) != 'a'){
                   ret += mask.charAt(x); 
                   x++;
               } 
               else {
                   if (mask.charAt(x) == '#') 
                           ret += txt.charAt(y).replace(/[^\d]+/gi,''); 
                   if (mask.charAt(x) == 'a') 
                           ret += txt.charAt(y).replace(/[\d]+/gi,''); 
                   y++; x++;
               }
       }
       
       // Retorno da função
       ret = (!r) ? ret : ret.reverse();       
       w.value = pre+ret+pos;
}


