package am.client;

import am.common.AppModule;

import oracle.jbo.client.remote.ApplicationModuleImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sat Apr 19 22:27:46 BRT 2014
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class AppModuleClient extends ApplicationModuleImpl implements AppModule {
    /**
     * This is the default constructor (do not remove).
     */
    public AppModuleClient() {
    }

    public int autenticarUsuario(String userid, String pass) {
        Object _ret =
            this.riInvokeExportedMethod(this,"autenticarUsuario",new String [] {"java.lang.String","java.lang.String"},new Object[] {userid, pass});
        return ((Integer)_ret).intValue();
    }
}
