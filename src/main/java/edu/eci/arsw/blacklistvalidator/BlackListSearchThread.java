package edu.eci.arsw.blacklistvalidator;
import java.util.LinkedList;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 * Clase que representa un hilo encargado de buscar una dirección IP en un rango
 * específico de servidores de listas negras. Cada instancia de esta clase
 * buscará en un subconjunto de servidores asignados y registrará las ocurrencias
 * de la IP en las listas negras.
 */
public class BlackListSearchThread extends Thread {
    private int startRange, endRange;     // rango de servidores que este hilo va a procesar
    private String ipAddress;    // direccion IP que se va a buscar en las listas negras
    private HostBlacklistsDataSourceFacade skds; // instancia de la fachada para acceder a las listas negras
    private LinkedList<Integer> blackListOcurrences;    // lista de numeros de las listas negras donde se encontro la IP
    private int ocurrencesCount, checkedListsCount;    // numero de veces que la IP fue encontrada en las listas negras y numero de listas negras revisadas por este hilo

    /**
     * Constructor para inicializar los parámetros del hilo.
     *
     * @param startRange Rango inicial del subconjunto de servidores que este hilo va a procesar.
     * @param endRange   Rango final del subconjunto de servidores que este hilo va a procesar.
     * @param ipAddress  Dirección IP que se va a buscar en las listas negras.
     * @param skds       Instancia de la fachada para consultar las listas negras.
     */
    public BlackListSearchThread(int startRange, int endRange, String ipAddress, HostBlacklistsDataSourceFacade skds){
        this.startRange = startRange;
        this.endRange = endRange;
        this.ipAddress = ipAddress;
        this.skds = skds;
        this.blackListOcurrences = new LinkedList<>();
        this.ocurrencesCount = 0;
        this.checkedListsCount = 0;
    }
    /**
     * Método que ejecuta la lógica de búsqueda de la IP en el rango de servidores
     * especificado. Este método se ejecuta cuando el hilo comienza.
     *
     * Revisa cada servidor dentro del rango y verifica si la IP está en la lista negra.
     * Si la IP es encontrada, se registra el número de la lista negra y se incrementa el conteo de ocurrencias.
     */
    @Override
    public void run(){
        for (int i = startRange; i<endRange; i++){
            checkedListsCount++;
            if (skds.isInBlackListServer(i, ipAddress)){
                blackListOcurrences.add(i);
                ocurrencesCount++;
            }
        }
    }

    /**
     * Devuelve el número de veces que la IP fue encontrada en las listas negras
     * dentro del rango que este hilo procesó.
     *
     * @return El número de ocurrencias de la IP en las listas negras.
     */    public int getOccurrences() {
        return ocurrencesCount;
    }

    /**
     * Devuelve el número de listas negras que este hilo revisó.
     *
     * @return El número de listas negras revisadas por este hilo.
     */
    public int getCheckedListsCount() {
        return checkedListsCount;
    }

    /**
     * Devuelve la lista de números de las listas negras donde la IP fue encontrada.
     *
     * @return Una lista de enteros que representan los números de las listas negras.
     */
    public LinkedList<Integer> getBlackListOcurrences() {
        return blackListOcurrences;
    }
}
