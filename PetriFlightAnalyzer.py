import re
import sys

if __name__ == "__main__":
    if len(sys.argv) < 2:
        transiciones = input("Por favor, introduce la secuencia de transiciones: ")
    else:
        transiciones = sys.argv[1]

regex='(T0)(.*?)(T1)(.*?)(((T2)(.*?)(T5)(.*?))|((T3)(.*?)(T4)(.*?)))(((T6)(.*?)(T9)(.*?)(T10)(.*?))|((T7)(.*?)(T8)(.*?)))(T11)(.*?)'

grupos=r'\g<2>\g<4>\g<8>\g<10>\g<13>\g<15>\g<19>\g<21>\g<23>\g<26>\g<28>\g<30>'

invariante1 = 0

invariante2 = 0

invariante3 = 0

invariante4 = 0

match = re.sub(regex,r'\g<1>\g<3>\g<7>\g<9>\g<12>\g<14>\g<18>\g<20>\g<22>\g<25>\g<27>\g<29>' , transiciones,1)

if re.search(r"T0T1T3T4T7T8T11",match):
    invariante1 = invariante1+1
elif re.search(r"T0T1T3T4T6T9T10T11",match):
    invariante2 = invariante2+1
elif re.search(r"T0T1T2T5T7T8T11",match):
    invariante3 = invariante3+1
elif re.search(r"T0T1T2T5T6T9T10T11",match):
    invariante4= invariante4+1

line=re.subn(regex,grupos,transiciones,1)
invariantes = 0


while line[1]>0:
    match = re.sub(regex,r'\g<1>\g<3>\g<7>\g<9>\g<12>\g<14>\g<18>\g<20>\g<22>\g<25>\g<27>\g<29>' , line[0],1)

    if re.search(r"T0T1T3T4T7T8T11",match):
        invariante1 = invariante1+1
    elif re.search(r"T0T1T3T4T6T9T10T11",match):
        invariante2 = invariante2+1
    elif re.search(r"T0T1T2T5T7T8T11",match):
        invariante3 = invariante3+1
    elif re.search(r"T0T1T2T5T6T9T10T11",match):
        invariante4= invariante4+1
        
    line=re.subn(regex,grupos,line[0],1)
    invariantes=invariantes+1


inv1 = ("T0T1T3T4T7T8T11",invariante1)      ##Agente 2 y cancelar
inv2 = ("T0T1T3T4T6T9T10T11",invariante2)   ##Agente 2 y confirmar
inv3 = ("T0T1T2T5T7T8T11",invariante3)      ##Agente 1 y cancelar
inv4 = ("T0T1T2T5T6T9T10T11",invariante4)   ##Agente 1 y confirmar
clientesAgente1 = inv3[1] + inv4[1]
clientesAgente2 = inv1[1] + inv2[1]
clientescancelar = inv1[1] + inv3[1]
clientesconfirmar = inv2[1] + inv4[1]

if line[0]=='':
  print('El test finalizo OK\nCantidad de invariantes:'+str(invariantes))
  print(inv1)
  print(inv2)
  print(inv3)
  print(inv4)
  print('Cantidad de clientes atendidos por Agente 1: '+str(clientesAgente1))
  print('Cantidad de clientes atendidos por Agente 2: '+str(clientesAgente2))
  print('Cantidad de clientes que confirmaron: '+str(clientesconfirmar))
  print('Cantidad de clientes que cancelaron: '+str(clientescancelar))  
    
else:
   print('El test finalizo FAIL han sobrado transiciones')

