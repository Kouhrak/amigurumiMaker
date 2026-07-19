package com.example.amigurumimaker.presentation.wallmodeler.ui.organisms

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.example.amigurumimaker.domain.model.BlockGeometry
import com.example.amigurumimaker.presentation.wallmodeler.theme.CadColor
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Size
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.LineNode
import io.github.sceneview.node.MeshNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import com.google.android.filament.RenderableManager
import com.google.android.filament.VertexBuffer
import com.google.android.filament.IndexBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer

@Composable
fun GeometryViewport(
    geometries: List<BlockGeometry>,
    modifier: Modifier = Modifier
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val environment = rememberEnvironment(environmentLoader)
    val cameraManipulator = rememberCameraManipulator()

    val fillMaterial = remember(materialLoader) {
        materialLoader.createUnlitColorInstance(CadColor.LightFill.toArgb())
    }
    val wireframeMaterial = remember(materialLoader) {
        materialLoader.createUnlitColorInstance(CadColor.LightWireframe.toArgb())
    }

    SceneView(
        modifier = modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        materialLoader = materialLoader,
        environmentLoader = environmentLoader,
        environment = environment,
        cameraManipulator = cameraManipulator,
        isOpaque = true
    ) {
        for (geo in geometries) {
            when (geo.vertices.size) {
                8 -> {
                    val minX = geo.vertices.minOf { it.x }
                    val maxX = geo.vertices.maxOf { it.x }
                    val minY = geo.vertices.minOf { it.y }
                    val maxY = geo.vertices.maxOf { it.y }
                    val minZ = geo.vertices.minOf { it.z }
                    val maxZ = geo.vertices.maxOf { it.z }

                    CubeNode(
                        size = Size(maxX - minX, maxY - minY, maxZ - minZ),
                        center = Position(
                            (minX + maxX) / 2f,
                            (minY + maxY) / 2f,
                            (minZ + maxZ) / 2f
                        ),
                        materialInstance = fillMaterial
                    )
                }
                6 -> {
                    val vertexCount = geo.vertices.size
                    val vertexData = FloatBuffer.allocate(vertexCount * 3)
                    for (v in geo.vertices) {
                        vertexData.put(v.x).put(v.y).put(v.z)
                    }
                    vertexData.flip()

                    val indexCount = geo.faceIndices.size * 3
                    val indexData = ShortBuffer.allocate(indexCount)
                    for (tri in geo.faceIndices) {
                        indexData.put(tri.first.toShort())
                            .put(tri.second.toShort())
                            .put(tri.third.toShort())
                    }
                    indexData.flip()

                    val vb = VertexBuffer.Builder()
                        .vertexCount(vertexCount)
                        .bufferCount(1)
                        .attribute(
                            VertexBuffer.VertexAttribute.POSITION,
                            3,
                            VertexBuffer.AttributeType.FLOAT
                        )
                        .build(engine)
                    vb.setBufferAt(engine, 0, vertexData)

                    val ib = IndexBuffer.Builder()
                        .indexCount(indexCount)
                        .bufferType(IndexBuffer.Builder.IndexType.USHORT)
                        .build(engine)
                    ib.setBuffer(engine, indexData)

                    MeshNode(
                        primitiveType = RenderableManager.PrimitiveType.TRIANGLES,
                        vertexBuffer = vb,
                        indexBuffer = ib,
                        materialInstance = fillMaterial
                    )
                }
            }

            // Wireframe edges for all geometry types
            for (edge in geo.edges) {
                val v0 = geo.vertices[edge.first]
                val v1 = geo.vertices[edge.second]
                LineNode(
                    start = Position(v0.x, v0.y, v0.z),
                    end = Position(v1.x, v1.y, v1.z),
                    materialInstance = wireframeMaterial
                )
            }
        }
    }
}
