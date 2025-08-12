package love.forte.simbot.codegen.codegen.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.naming.plus

// TODO
@Suppress("unused")
object SpringNames {
    //val springPkg = "org.springframework".parseToPackageName()
    val springStereotypePkg = "org.springframework.stereotype".parseToPackageName()
    val springBindPkg = "org.springframework.web.bind.annotation".parseToPackageName()
    val springContextPkg = "org.springframework.context.annotation".parseToPackageName()
    val springBeanPkg = "org.springframework.beans.factory.annotation".parseToPackageName()
    val springBootPkg = "org.springframework.boot".parseToPackageName()

    val componentAno = ClassName(springStereotypePkg, "Component")
    val serviceAno = ClassName(springStereotypePkg, "Service")
    val repositoryAno = ClassName(springStereotypePkg, "Repository")
    val controllerAno = ClassName(springBindPkg, "Controller")
    val restControllerAno = ClassName(springBindPkg, "RestController")
    val configurationAno = ClassName(springContextPkg, "Configuration")
    val autoWiredAno = ClassName(springBeanPkg, "Autowired")
    val qualifierAno = ClassName(springBeanPkg, "Qualifier")
    val valueAno = ClassName(springBeanPkg, "Value")
    val requestMappingAno = ClassName(springBindPkg, "RequestMapping")
    val getMappingAno = ClassName(springBindPkg, "GetMapping")
    val postMappingAno = ClassName(springBindPkg, "PostMapping")
    val putMappingAno = ClassName(springBindPkg, "PutMapping")
    val deleteMappingAno = ClassName(springBindPkg, "DeleteMapping")
    val requestBodyAno = ClassName(springBindPkg, "RequestBody")
    val pathVariableAno = ClassName(springBindPkg, "PathVariable")
    val requestParamAno = ClassName(springBindPkg, "RequestParam")

    // Spring Boot annotations
    private val subPackageAutoconfigureCondition = "autoconfigure.condition".parseToPackageName()
    private val springBootAutoconfigureCondition = springBootPkg + subPackageAutoconfigureCondition

    val springBootApplicationAno = ClassName(springBootPkg, "SpringBootApplication")
    val enableAutoConfigurationAno = ClassName(springBootPkg, "EnableAutoConfiguration")
    val conditionalOnMissingBeanAno = ClassName(springBootAutoconfigureCondition, "ConditionalOnMissingBean")
    val conditionalOnBeanAno = ClassName(springBootAutoconfigureCondition, "ConditionalOnBean")
    val conditionalOnPropertyAno = ClassName(springBootAutoconfigureCondition, "ConditionalOnProperty")
    val conditionalOnClassAno = ClassName(springBootAutoconfigureCondition, "ConditionalOnClass")
}