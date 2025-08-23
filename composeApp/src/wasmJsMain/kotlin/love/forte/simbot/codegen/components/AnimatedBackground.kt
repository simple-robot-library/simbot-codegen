package love.forte.simbot.codegen.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * 动态粒子数据类
 */
private data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float, // x方向速度
    var vy: Float, // y方向速度
    val radius: Float,
    val alpha: Float
)

/**
 * 动态连线背景组件
 * 在页面底部绘制不停变化、随机的点、线连接动态背景
 */
@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    maxConnectionDistance: Float = 120f,
    particleSpeed: Float = 0.65f
) {
    val density = LocalDensity.current
    val colorScheme = MaterialTheme.colorScheme

    // 将dp转换为px
    val connectionDistancePx = with(density) { maxConnectionDistance.dp.toPx() }

    // 粒子状态
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    var canvasSize by remember { mutableStateOf(Offset.Zero) }

    // 动画时间状态 - 使用更合理的帧率
    val animationTime by rememberInfiniteTransition(label = "backgroundAnimation")
        .animateFloat(
            initialValue = 0f,
            targetValue = 1000f, // 更大的目标值以提供更精细的时间控制
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 50, easing = LinearEasing), // 约20fps，更稳定的性能
                repeatMode = RepeatMode.Restart
            ),
            label = "timeAnimation"
        )

    // 初始化粒子
    LaunchedEffect(canvasSize, particleCount) {
        if (canvasSize != Offset.Zero) {
            particles = List(particleCount) {
                createRandomParticle(canvasSize.x, canvasSize.y, particleSpeed)
            }
        }
    }

    // 更新粒子位置
    LaunchedEffect(animationTime) {
        if (particles.isNotEmpty() && canvasSize != Offset.Zero) {
            particles = particles.map { particle ->
                updateParticle(particle, canvasSize.x, canvasSize.y, particleSpeed)
            }
        }
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        canvasSize = Offset(size.width, size.height)

        if (particles.isNotEmpty()) {
            // 使用更明显的颜色以确保在所有主题下都可见
            val baseParticleColor = colorScheme.primary
            val baseConnectionColor = colorScheme.secondary

            drawParticlesAndConnections(
                particles = particles,
                connectionDistance = connectionDistancePx,
                particleColor = baseParticleColor.copy(alpha = 0.65f), // 更高的透明度
                connectionColor = baseConnectionColor.copy(alpha = 0.4f) // 使用次要颜色提高对比度
            )
        }
    }
}

/**
 * 创建随机粒子
 */
private fun createRandomParticle(canvasWidth: Float, canvasHeight: Float, baseSpeed: Float): Particle {
    return Particle(
        x = Random.nextFloat() * canvasWidth,
        y = Random.nextFloat() * canvasHeight,
        vx = (Random.nextFloat() - 0.5f) * 2 * baseSpeed,
        vy = (Random.nextFloat() - 0.5f) * 2 * baseSpeed,
        radius = Random.nextFloat() * 2f + 1.5f, // 减小粒子半径：1.5-3.5像素
        alpha = Random.nextFloat() * 0.4f + 0.6f // 增加透明度：0.6-1.0
    )
}

/**
 * 更新粒子位置
 */
private fun updateParticle(particle: Particle, canvasWidth: Float, canvasHeight: Float, baseSpeed: Float): Particle {
    var newX = particle.x + particle.vx
    var newY = particle.y + particle.vy
    var newVx = particle.vx
    var newVy = particle.vy

    // 边界碰撞检测和反弹
    if (newX <= 0 || newX >= canvasWidth) {
        newVx = -newVx
        newX = newX.coerceIn(0f, canvasWidth)
    }
    if (newY <= 0 || newY >= canvasHeight) {
        newVy = -newVy
        newY = newY.coerceIn(0f, canvasHeight)
    }

    // 添加微小的随机扰动，让运动更自然
    newVx += (Random.nextFloat() - 0.5f) * 0.02f * baseSpeed
    newVy += (Random.nextFloat() - 0.5f) * 0.02f * baseSpeed

    // 限制速度范围
    val maxSpeed = baseSpeed * 2
    newVx = newVx.coerceIn(-maxSpeed, maxSpeed)
    newVy = newVy.coerceIn(-maxSpeed, maxSpeed)

    return particle.copy(x = newX, y = newY, vx = newVx, vy = newVy)
}

/**
 * 绘制粒子和连接线
 */
private fun DrawScope.drawParticlesAndConnections(
    particles: List<Particle>,
    connectionDistance: Float,
    particleColor: Color,
    connectionColor: Color
) {
    // 绘制连接线
    for (i in particles.indices) {
        for (j in i + 1 until particles.size) {
            val particle1 = particles[i]
            val particle2 = particles[j]

            val distance = sqrt(
                (particle1.x - particle2.x).pow(2) +
                        (particle1.y - particle2.y).pow(2)
            )

            if (distance <= connectionDistance) {
                val alpha = (1 - distance / connectionDistance) * connectionColor.alpha
                drawLine(
                    color = connectionColor.copy(alpha = alpha),
                    start = Offset(particle1.x, particle1.y),
                    end = Offset(particle2.x, particle2.y),
                    strokeWidth = 1f
                )
            }
        }
    }

    // 绘制粒子
    particles.forEach { particle ->
        drawCircle(
            color = particleColor.copy(alpha = particle.alpha),
            radius = particle.radius,
            center = Offset(particle.x, particle.y)
        )
    }
}