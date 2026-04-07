"""
=============================================================================
PROYECTO: AgroConecta - Microservicio de Analítica y Recomendaciones
MÓDULOS: 
  1. Motor de Recomendaciones (RF07) - [ACTUALIZADO CON GEOLOCALIZACIÓN]
  2. Analítica de Ventas (RF08)
TECNOLOGÍAS: Python, Flask, Pandas, MySQL Connector
=============================================================================
"""

from flask import Flask, jsonify
import mysql.connector
import pandas as pd

app = Flask(__name__)

# CONFIGURACIÓN DE BASE DE DATOS

def obtener_conexion():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="", # Colocar contraseña si la de tu MySQL local tiene una
        database="agroconecta"
    )


# MÓDULO 1: MOTOR DE RECOMENDACIONES (RF07)

@app.route('/api/recomendaciones', methods=['GET'])
def recomendar_productos():
    try:
        # Abrimos la conexión
        conexion = obtener_conexion()
        cursor = conexion.cursor(dictionary=True)
        
        query = """
            SELECT id, nombre, categoria, precio, ubicacion, imagen_url, latitud, longitud 
            FROM producto 
            ORDER BY precio ASC 
            LIMIT 5
        """
        cursor.execute(query)
        recomendaciones = cursor.fetchall()
        
        # Cerramos aquí mismo si todo sale bien
        cursor.close()
        conexion.close()
        
        return jsonify({
            "status": "success",
            "mensaje": "Recomendaciones generadas con éxito (Incluye datos espaciales)",
            "data": recomendaciones
        }), 200

    except Exception as e:
        # Si algo falla (hasta la conexión), lo capturamos aquí
        return jsonify({"status": "error", "mensaje": str(e)}), 500

# MÓDULO 2: ANALÍTICA DE VENTAS CON PANDAS (RF08)

@app.route('/api/analitica/ventas', methods=['GET'])
def analitica_ventas():
    try:
        # Abrimos la conexión
        conexion = obtener_conexion()
        
        query_pedidos = """
            SELECT p.id as pedido_id, p.cantidad, p.total, p.fecha, 
                   pr.nombre as producto, pr.categoria, pr.ubicacion
            FROM pedido p
            INNER JOIN producto pr ON p.producto_id = pr.id
        """
        df = pd.read_sql(query_pedidos, conexion)
        
        # Cerramos la conexión inmediatamente después de leer con pandas
        conexion.close()
        
        if df.empty:
            return jsonify({"status": "info", "mensaje": "No hay ventas registradas aún."})

        ingresos_totales = float(df['total'].sum())
        
        ventas_por_categoria = df.groupby('categoria')['cantidad'].sum().reset_index()
        categoria_top = ventas_por_categoria.loc[ventas_por_categoria['cantidad'].idxmax()].to_dict()
        
        ticket_promedio = float(df['total'].mean())

        reporte = {
            "ingresos_totales_cop": ingresos_totales,
            "ticket_promedio_cop": round(ticket_promedio, 2),
            "categoria_mas_vendida": {
                "categoria": categoria_top['categoria'],
                "unidades_vendidas": int(categoria_top['cantidad'])
            },
            "total_transacciones": len(df)
        }

        return jsonify({
            "status": "success",
            "mensaje": "Reporte analítico generado con Pandas",
            "métricas": reporte
        }), 200

    except Exception as e:
        return jsonify({"status": "error", "mensaje": str(e)}), 500

# INICIO DEL SERVIDOR FLASK
if __name__ == '__main__':
    print("Microservicio de Python para AgroConecta iniciado en puerto 5000")
    app.run(debug=True, port=5000)
