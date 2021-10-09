package com.zmendax.debug.context;

import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Desc AnnotationConfigAppCtxTest
 * @Author zhaolele@meituan.com
 * @Date 2021/10/9 上午10:55
 */
public class AnnotationConfigAppCtxTest {

	@Test
	public void test() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(Config.class);
		context.refresh();
		UserService bean = context.getBean(UserService.class);
		bean.test();
	}

	@Configuration
	@Import(AopImportSelector.class)
	@EnableAspectJAutoProxy(exposeProxy = true)
	public static class Config {
	}

	// 使用 @Import 和 ImportSelector 搭配，就可以省去 XML 配置
	public static class AopImportSelector implements ImportSelector {
		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			return new String[]{
					UserService.class.getName(),
			};
		}
	}

	public static class UserService {
		private String desc = "testBean";

		public String getDesc() {
			System.out.println("getDesc");
			this.test();
			System.out.println("--this----------getDesc");
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
			// 使用 @EnableAspectJAutoProxy(exposeProxy = true) 打开 exposeProxy = true
			// 则必须这样写，才能获取到当前的代理对象，然后调用的方法才是被 AOP 处理后的方法。
			// 使用 this.methodName() 调用，依然调用的是原始的、未经 AOP 处理的方法
			((UserService) AopContext.currentProxy()).test();
			System.out.println("--AopContext----setDesc");
		}

		public void test() {
			System.out.println("----------------test");
		}

	}
}
