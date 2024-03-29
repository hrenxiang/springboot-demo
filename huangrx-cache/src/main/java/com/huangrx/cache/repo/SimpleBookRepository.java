package com.huangrx.cache.repo;

import com.huangrx.cache.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class SimpleBookRepository implements BookRepository {

	private static final Logger logger = LoggerFactory.getLogger(SimpleBookRepository.class);

	/**
	 * condition 在什么条件下 缓存结果
	 * unless 除非结果怎么怎么样的时候被缓存 !#result.auto.equals('vector') 当结果的auto不等于vector的时候被缓存
	 * unless Default is "", meaning that caching is never vetoed.(需要慢慢体会) 否的时候生效，真的时候失效
	 * https://docs.spring.io/spring/docs/5.0.7.RELEASE/javadoc-api/
	 * @param isbn
	 * @return
	 */
	@Override
	@Cacheable(cacheNames = "books")
	public Book getByIsbn(String isbn) {
		simulateSlowService();
		System.out.println("======== 没有走缓存 ========");
		return new Book(isbn, "Some book", "vector");
	}

	@Override
	@Cacheable(value = "bookCache", key = "#root.methodName")
	public Book getByIsbnNoArgs() {
		simulateSlowService();
		System.out.println("======== 没有走缓存 ========");
		return new Book("123456", "Some book", "vector");
	}

	/**
	 * 最好指定key,此时会根据key将cache中的内容更新为最新
	 * @param book
	 * @return
	 */
	@Override
	@CachePut(cacheNames = "books", key = "#book.isbn.hashCode()") // 方法的返回值会被更新到缓存中
	public Book update(Book book) {
		logger.info("更新book");
		return book;
	}

	/**
	 * beforeInvocation = true 意味着不管业务如何出错，缓存已清空
	 * beforeInvocation = false 等待方法处理完之后再清空缓存，缺点是如果逻辑出异常了，会导致缓存不会被清空
	 */
	@Override
	@CacheEvict(cacheNames = "books", allEntries = true, beforeInvocation = false)
	public void clear() {
		logger.warn("清空books缓存数据");
		throw new RuntimeException("测试 beforeInvocation = fasle");
	}


	/**
	 * 模拟慢查询
	 */
	private void simulateSlowService() {
		try {
			long time = 3000L;
			Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}