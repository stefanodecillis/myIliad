package com.phone.step.dataMeter;

public class Constants {

    static final public String iliadWeb = "https://www.iliad.it/account/";
    static final public String payload = "login-ident=%%&login-pwd=££";
    static final public String cookieSession = "ACCOUNT_SESSID=YYY; RGPD=-1";
    static final public String reportAddress = "";

    static final public String ID_KEY = "userID";
    static final public String PSW_KEY = "userPsw";
    static final public String CURL_SITE = "curl -Is https://www.iliad.it/account/fsd/ -m 5 | head -";

    //layout
    static final public String CREDIT_TXT = "Credito: X";
    static final public String INFO_LOGIN = "Le credenziali per l'applicazione sono le stesse del tuo account iliad. Puoi trovare tali dati sulla prima mail ricevuta da iliad durante la registrazione oppure tra i documenti ricevuti \n \n I dati inseriti saranno salvati solo sul tuo telefono. Per maggiori informazioni, leggere la descrizione dell'applicazione sul play store";
}
