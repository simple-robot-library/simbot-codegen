/**
 * Optimized Loading Screen JavaScript
 * Extracted from inline scripts for better caching and performance
 */

// Optimized particle creation - reduced from 20 to 12 particles for better performance
function createFloatingParticles() {
    const container = document.getElementById('floating-particles');
    if (!container) return;
    
    const particleCount = 12; // Reduced from 20 for better performance
    const fragment = document.createDocumentFragment(); // Use fragment for better performance
    
    for (let i = 0; i < particleCount; i++) {
        const particle = document.createElement('div');
        particle.className = 'float-particle';
        
        // Pre-calculate values to reduce runtime calculations
        const left = Math.random() * 100;
        const top = Math.random() * 100;
        const delay = Math.random() * 8;
        const size = Math.random() * 2 + 1;
        const opacity = Math.random() * 0.3 + 0.1;
        
        // Apply styles in a single operation
        particle.style.cssText = `
            left: ${left}%;
            top: ${top}%;
            animation-delay: ${delay}s;
            width: ${size}px;
            height: ${size}px;
            opacity: ${opacity};
        `;
        
        fragment.appendChild(particle);
    }
    
    container.appendChild(fragment);
}

// Optimized code line animation with better memory management
function animateCodeLines() {
    const codeLine = document.getElementById('code-line');
    if (!codeLine) return;
    
    const codeMessages = [
        "正在初始化 Simbot Codegen ...",
        "加载超级人类资源...",
        "法欧莉正在为您打扫布局...",
        "思考宇宙的答案...",
        "与法欧莉建立精神链接..."
    ];
    
    let index = 0;
    
    return setInterval(() => {
        codeLine.textContent = codeMessages[index];
        index = (index + 1) % codeMessages.length;
    }, 4000);
}

// Optimized progress simulation with better performance
function simulateProgress(progressBar, progressText) {
    let progress = 0;
    
    return setInterval(() => {
        // Optimized progress calculation
        const increment = Math.random() * 3;
        progress += increment * (1 - progress / 100) * 0.7;
        
        if (progress > 99) progress = 99; // Cap at 99% until actual load complete
        
        // Use requestAnimationFrame for smoother updates
        requestAnimationFrame(() => {
            progressBar.style.width = progress + '%';
            progressText.textContent = `加载应用中... ${Math.round(progress)}%`;
        });
        
    }, 300); // Increased from 200ms to 300ms for better performance
}

// Optimized script loading with better error handling
function loadComposeScript() {
    return new Promise((resolve, reject) => {
        const script = document.createElement('script');
        script.src = 'composeApp.js';
        script.type = 'application/javascript';
        
        script.onload = resolve;
        script.onerror = reject;
        
        document.head.appendChild(script);
    });
}

// Theme detection and application
function applyTheme() {
    try {
        const savedTheme = localStorage.getItem('simbot_codegen_theme_preference');
        const isDarkTheme = savedTheme === 'DARK';
        
        if (isDarkTheme) {
            document.documentElement.classList.add('dark-theme');
        } else {
            document.documentElement.classList.remove('dark-theme');
        }
        
        console.log(`Applied theme: ${isDarkTheme ? 'DARK' : 'LIGHT'}`);
    } catch (e) {
        // Fallback to light theme on error
        document.documentElement.classList.remove('dark-theme');
        console.warn('Failed to load theme preference, using light theme as fallback');
    }
}

// Main initialization function
function initializeLoadingScreen() {
    const loadingContainer = document.getElementById('loading-container');
    const progressBar = document.getElementById('progress-bar');
    const progressText = document.getElementById('progress-text');
    
    if (!loadingContainer || !progressBar || !progressText) {
        console.error('Loading screen elements not found');
        return;
    }
    
    // Apply theme before showing animations
    applyTheme();
    
    // Create optimized animations
    createFloatingParticles();
    const codeLineInterval = animateCodeLines();
    const progressInterval = simulateProgress(progressBar, progressText);
    
    // Load the main application script
    loadComposeScript()
        .then(() => {
            // Script loaded successfully
            progressBar.style.width = '100%';
            progressText.textContent = '应用加载完成 (100%)';
            clearInterval(progressInterval);
        })
        .catch(() => {
            // Script loading failed
            progressText.textContent = '加载失败，请刷新页面重试';
            clearInterval(progressInterval);
        });
    
    // Store intervals for cleanup if needed
    window.loadingIntervals = {
        codeLineInterval,
        progressInterval
    };
}

// Font loading notification functions (called from Compose)
window.notifyFontLoadingStart = function() {
    console.log("Font loading started");
    const progressText = document.getElementById('progress-text');
    const codeLine = document.getElementById('code-line');
    
    if (progressText) {
        progressText.textContent = '加载字体中...';
    }
    if (codeLine) {
        codeLine.textContent = '正在加载字体文件...';
    }
};

window.notifyFontLoadingComplete = function() {
    console.log("Font loading completed");
    const loadingContainer = document.getElementById('loading-container');
    const progressText = document.getElementById('progress-text');
    
    // Clean up intervals
    if (window.loadingIntervals) {
        clearInterval(window.loadingIntervals.codeLineInterval);
        clearInterval(window.loadingIntervals.progressInterval);
    }
    
    if (progressText) {
        progressText.textContent = '字体加载完成！';
    }
    
    // Optimized fade-out with better performance
    if (loadingContainer) {
        requestAnimationFrame(() => {
            loadingContainer.style.transition = 'opacity 0.5s ease-out';
            loadingContainer.style.opacity = '0';
            
            setTimeout(() => {
                loadingContainer.style.display = 'none';
                loadingContainer.remove();
                
                // Clear references for garbage collection
                window.loadingIntervals = null;
            }, 500);
        });
    }
};

// Initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeLoadingScreen);
} else {
    // DOM already loaded
    initializeLoadingScreen();
}
