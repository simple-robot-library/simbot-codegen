package love.forte.simbot.codegen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * 毛玻璃效果修饰符
 * 通过多层半透明背景和纹理模拟毛玻璃效果，无需依赖CSS或DOM
 * 现在支持形状裁剪，确保效果不会溢出圆角等边界
 * 
 * @param isActive 是否启用毛玻璃效果
 * @param intensity 效果强度，范围0.0-1.0
 * @param backgroundColor 基础背景色，如果为null则使用主题色
 * @param shape 裁剪形状，如果为null则不进行形状裁剪
 */
@Composable
fun Modifier.frostedGlass(
    isActive: Boolean,
    intensity: Float = 1.0f,
    backgroundColor: Color? = null,
    shape: Shape? = null
): Modifier {
    val colorScheme = MaterialTheme.colorScheme
    val baseColor = backgroundColor ?: colorScheme.surface
    
    // 动画过渡效果强度
    val animatedIntensity by animateFloatAsState(
        targetValue = if (isActive) intensity else 0f,
        label = "frostedGlassIntensity"
    )
    
    return this.then(
        if (animatedIntensity > 0f) {
            Modifier.drawBehind {
                drawFrostedGlassEffect(
                    baseColor = baseColor,
                    primaryColor = colorScheme.primary,
                    surfaceVariant = colorScheme.surfaceVariant,
                    intensity = animatedIntensity,
                    shape = shape
                )
            }
        } else {
            Modifier
        }
    )
}

/**
 * 绘制毛玻璃效果的核心函数
 * 通过多层渐变、噪声纹理和透明度变化模拟真实的毛玻璃效果
 * 现在支持形状裁剪，确保效果在指定形状内绘制
 */
private fun DrawScope.drawFrostedGlassEffect(
    baseColor: Color,
    primaryColor: Color,
    surfaceVariant: Color,
    intensity: Float,
    shape: Shape? = null
) {
    val width = size.width
    val height = size.height
    
    // 定义绘制操作的lambda函数
    val drawOperations = {
        // 第一层：基础半透明背景
        drawRect(
            color = baseColor.copy(alpha = 0.85f * intensity),
            size = size
        )
        
        // 第二层：增强的高斯模糊效果，多层渐变模拟真实模糊
        drawEnhancedBlurEffect(width, height, intensity, surfaceVariant, primaryColor)
        
        // 第三层：添加纹理噪声，模拟毛玻璃的细微纹理
        drawGlassTexture(width, height, intensity, surfaceVariant)
        
        // 第四层：边缘高光效果，增强玻璃质感
        drawGlassHighlights(width, height, intensity, primaryColor)
    }
    
    // 如果提供了形状，使用裁剪路径绘制；否则直接绘制
    if (shape != null) {
        val path = Path().apply {
            addOutline(shape.createOutline(size, layoutDirection, this@drawFrostedGlassEffect))
        }
        clipPath(path) {
            drawOperations()
        }
    } else {
        drawOperations()
    }
}

/**
 * 绘制玻璃纹理噪声
 * 通过计算生成的伪随机点创建类似磨砂玻璃的纹理效果
 * 现在使用增强版本，支持多种大小和透明度的纹理点
 */
private fun DrawScope.drawGlassTexture(
    width: Float,
    height: Float,
    intensity: Float,
    color: Color
) {
    // 使用增强的纹理点生成算法
    val enhancedTexturePoints = generateEnhancedTexturePoints(width, height)
    
    enhancedTexturePoints.forEach { (point, size, alpha) ->
        drawCircle(
            color = color.copy(alpha = alpha * intensity),
            radius = size,
            center = point
        )
    }
}

/**
 * 绘制玻璃高光效果
 * 在边缘和特定区域添加微妙的高光，增强玻璃的立体感
 */
private fun DrawScope.drawGlassHighlights(
    width: Float,
    height: Float,
    intensity: Float,
    highlightColor: Color
) {
    val highlightAlpha = 0.12f * intensity
    
    // 顶部高光
    val topHighlight = Brush.verticalGradient(
        colors = listOf(
            highlightColor.copy(alpha = highlightAlpha),
            Color.Transparent
        ),
        startY = 0f,
        endY = height * 0.3f
    )
    
    drawRect(
        brush = topHighlight,
        size = size
    )
    
    // 左侧高光
    val leftHighlight = Brush.horizontalGradient(
        colors = listOf(
            highlightColor.copy(alpha = highlightAlpha * 0.7f),
            Color.Transparent
        ),
        startX = 0f,
        endX = width * 0.2f
    )
    
    drawRect(
        brush = leftHighlight,
        size = size
    )
}

/**
 * 生成纹理点位置
 * 使用确定性算法生成看起来随机但可重现的纹理点
 */
private fun generateTexturePoints(width: Float, height: Float): List<Offset> {
    val points = mutableListOf<Offset>()
    val density = 0.3f // 控制纹理密度
    val stepX = width / (width * density / 10)
    val stepY = height / (height * density / 10)
    
    var x = 0f
    while (x < width) {
        var y = 0f
        while (y < height) {
            // 使用三角函数创建伪随机偏移
            val offsetX = sin(x * 0.01f + y * 0.007f) * 3f
            val offsetY = cos(y * 0.013f + x * 0.009f) * 3f
            
            // 只有满足特定条件的点才会被绘制，创建稀疏的纹理效果
            if (sin(x * 0.02f) * cos(y * 0.015f) > 0.1f) {
                points.add(Offset(x + offsetX, y + offsetY))
            }
            
            y += stepY
        }
        x += stepX
    }
    
    return points
}

/**
 * 生成增强的纹理点位置
 * 返回包含位置、大小和透明度的纹理点，创建更自然的效果
 */
private fun generateEnhancedTexturePoints(width: Float, height: Float): List<Triple<Offset, Float, Float>> {
    val points = mutableListOf<Triple<Offset, Float, Float>>()
    val density = 0.4f // 增加密度获得更丰富的纹理
    val stepX = width / (width * density / 8)
    val stepY = height / (height * density / 8)
    
    var x = 0f
    while (x < width) {
        var y = 0f
        while (y < height) {
            // 使用更复杂的函数创建更自然的分布
            val noiseX = sin(x * 0.008f + y * 0.012f) * 4f
            val noiseY = cos(y * 0.011f + x * 0.007f) * 4f
            
            // 使用多个条件创建不同类型的纹理点
            val threshold = sin(x * 0.015f) * cos(y * 0.018f)
            if (threshold > -0.2f) {
                // 计算点的大小 - 基于位置的变化
                val sizeVariation = (sin(x * 0.02f + y * 0.025f) + 1f) * 0.5f
                val pointSize = 0.3f + sizeVariation * 1.2f
                
                // 计算透明度 - 创建不均匀分布
                val alphaVariation = (cos(x * 0.013f + y * 0.019f) + 1f) * 0.5f
                val pointAlpha = 0.04f + alphaVariation * 0.08f
                
                points.add(
                    Triple(
                        Offset(x + noiseX, y + noiseY),
                        pointSize,
                        pointAlpha
                    )
                )
            }
            
            y += stepY
        }
        x += stepX
    }
    
    return points
}

/**
 * 增强的高斯模糊效果
 * 通过多层不同半径的渐变叠加，更真实地模拟高斯模糊
 */
private fun DrawScope.drawEnhancedBlurEffect(
    width: Float,
    height: Float,
    intensity: Float,
    surfaceVariant: Color,
    primaryColor: Color
) {
    // 模拟多层高斯模糊 - 使用多个不同半径的径向渐变
    val blurLayers = listOf(
        // 大半径，低权重 - 模拟远距离模糊
        Triple(width * 1.2f, 0.08f, Offset(width * 0.5f, height * 0.5f)),
        // 中等半径，中等权重 - 主要模糊效果
        Triple(width * 0.8f, 0.15f, Offset(width * 0.3f, height * 0.2f)),
        Triple(width * 0.6f, 0.12f, Offset(width * 0.7f, height * 0.6f)),
        // 小半径，高权重 - 细节模糊
        Triple(width * 0.4f, 0.2f, Offset(width * 0.2f, height * 0.8f)),
        Triple(width * 0.3f, 0.18f, Offset(width * 0.8f, height * 0.3f))
    )
    
    blurLayers.forEach { (radius, weight, center) ->
        // 为每层使用轻微不同的颜色，增加真实感
        val layerColor = when {
            radius > width * 0.8f -> surfaceVariant.copy(alpha = weight * intensity)
            radius > width * 0.5f -> primaryColor.copy(alpha = weight * 0.8f * intensity)
            else -> surfaceVariant.copy(alpha = weight * 1.2f * intensity)
        }
        
        val blurGradient = Brush.radialGradient(
            colors = listOf(
                layerColor,
                layerColor.copy(alpha = layerColor.alpha * 0.7f),
                layerColor.copy(alpha = layerColor.alpha * 0.3f),
                Color.Transparent
            ),
            center = center,
            radius = radius
        )
        
        drawRect(
            brush = blurGradient,
            size = size
        )
    }
}

/**
 * 毛玻璃容器组件
 * 为内容提供毛玻璃背景效果的便捷容器
 * 
 * @param isActive 是否启用毛玻璃效果
 * @param intensity 效果强度
 * @param modifier 修饰符
 * @param content 内容
 */
@Composable
fun FrostedGlassContainer(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    intensity: Float = 1.0f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.frostedGlass(
            isActive = isActive,
            intensity = intensity
        )
    ) {
        content()
    }
}