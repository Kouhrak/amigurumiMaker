package com.example.amigurumimaker.presentation.wallmodeler.ui.organisms

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.amigurumimaker.domain.model.Float3
import com.example.amigurumimaker.domain.model.RevolutionMesh
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
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
    revolutionMesh: RevolutionMesh?,
    wireframeEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val environment = rememberEnvironment(environmentLoader)
    val cameraManipulator = rememberCameraManipulator()

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
        revolutionMesh?.segments?.forEach { segment ->
            if (segment.vertices.isEmpty() || segment.indices.isEmpty()) return@forEach

            val argb = segmentColorToArgb(segment.color)
            val fillMaterial = remember(argb, materialLoader) {
                materialLoader.createColorInstance(argb)
            }

            val vertexCount = segment.vertices.size
            val vertexData = FloatBuffer.allocate(vertexCount * 3)
            for (v in segment.vertices) {
                vertexData.put(v.x).put(v.y).put(v.z)
            }
            vertexData.flip()

            val indexCount = segment.indices.size
            val indexData = ShortBuffer.allocate(indexCount)
            for (idx in segment.indices) {
                indexData.put(idx.toShort())
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

            if (wireframeEnabled) {
                val wireframeMaterial = remember(materialLoader) {
                    materialLoader.createUnlitColorInstance(0xFF1E293B.toInt())
                }

                val edgeSet = mutableSetOf<Pair<Int, Int>>()
                for (triIdx in segment.indices.indices step 3) {
                    val i0 = segment.indices[triIdx]
                    val i1 = segment.indices[triIdx + 1]
                    val i2 = segment.indices[triIdx + 2]
                    edgeSet.add(minOf(i0, i1) to maxOf(i0, i1))
                    edgeSet.add(minOf(i1, i2) to maxOf(i1, i2))
                    edgeSet.add(minOf(i2, i0) to maxOf(i2, i0))
                }

                for ((a, b) in edgeSet) {
                    val v0 = segment.vertices[a]
                    val v1 = segment.vertices[b]
                    LineNode(
                        start = Position(v0.x, v0.y, v0.z),
                        end = Position(v1.x, v1.y, v1.z),
                        materialInstance = wireframeMaterial
                    )
                }
            }
        }
    }
}

private fun segmentColorToArgb(color: Float3): Int {
    val r = (color.x * 255f).toInt().coerceIn(0, 255)
    val g = (color.y * 255f).toInt().coerceIn(0, 255)
    val b = (color.z * 255f).toInt().coerceIn(0, 255)
    return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
}
