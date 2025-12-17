# Sistema Bancario – Banco Estado

Proyecto desarrollado en **Java** para la asignatura **Modelamiento de Procesos e Información**.  
El sistema permite la gestión de clientes, cuentas bancarias y sucursales, junto con operaciones financieras básicas, utilizando una arquitectura **MVC** y persistencia mediante serialización.

---

## Requisitos Básicos

- **Java JDK 8 o superior**
- **IntelliJ IDEA** (Community o Ultimate)
- Sistema operativo compatible con Java (Windows / Linux / macOS)
- **Git** instalado (para clonar el repositorio)

---

## Clonar el Repositorio

Desde una terminal o consola:

```bash
git clone https://github.com/MatiasEng/Sistema-Banco-Estado.git
````

Luego, abrir el proyecto en **IntelliJ IDEA**:

1. Abrir IntelliJ IDEA
2. Seleccionar **File → Open**
3. Elegir la carpeta del repositorio clonado
4. Esperar a que IntelliJ indexe el proyecto

---

## ⚙️ Configuración en IntelliJ IDEA

1. Verificar el JDK:

   * Ir a **File → Project Structure → Project**
   * Seleccionar **Project SDK: Java 8 (o superior)**

2. Confirmar estructura del proyecto:

   * El código fuente debe estar dentro de la carpeta `src/`

3. No se requieren dependencias externas.

---

## Ejecución del Programa (Tutorial)

1. En IntelliJ, ubicar la clase principal del sistema (clase que contiene el método `main`).
2. Hacer clic derecho sobre la clase.
3. Seleccionar **Run 'NombreClase'**.
4. El sistema iniciará mostrando la interfaz gráfica (Java Swing).

> 💡 En ejecuciones posteriores, IntelliJ permite iniciar el sistema desde el botón **Run ▶**.

---

## Persistencia y Archivos Generados

El sistema guarda automáticamente la información en archivos locales utilizando serialización:

* `clientes.dat`
  Contiene los clientes y sus cuentas bancarias.

* `sucursales.dat`
  Contiene las sucursales y sus ejecutivos.

Estos archivos se crean **en la raíz del proyecto** al ejecutar el programa por primera vez.

---

## Contratos de Cuentas

Cada vez que se crea una cuenta bancaria, el sistema genera un contrato en formato `.txt`.

* Carpeta de contratos:

  ```
  /contratos/
  ```

* Formato del archivo:

  ```
  ContratoCuenta-<numeroCuenta>.txt
  ```

Ejemplo:

```
contratos/ContratoCuenta-123456789012.txt
```

La carpeta `contratos` se crea automáticamente si no existe.

---

## Estructura General del Proyecto

```
Sistema-Banco-Estado/
│
├── src/
│   ├── modelo/
│   ├── controlador/
│   └── vista/
│
├── contratos/
│   └── ContratoCuenta-XXXX.txt
│
├── clientes.dat
├── sucursales.dat
│
└── README.md
```

---

## Consideraciones Importantes

* Si se eliminan los archivos `.dat`, el sistema comenzará sin datos previos.
* Los datos se cargan automáticamente al iniciar el programa.
* Todas las operaciones relevantes guardan cambios de forma automática.

