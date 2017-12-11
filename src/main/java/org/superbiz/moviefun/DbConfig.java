package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
@Configuration
public class DbConfig {

    private PlatformTransactionManager transactionManager;

    @Bean("albumsDS")
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDatabaseName("albums");
        return dataSource;
    }


    @Bean("moviesDS")
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDatabaseName("movies");
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter getAdapter( )
    {

        HibernateJpaVendorAdapter hibernateJpaVendorAdapter =new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);

        return hibernateJpaVendorAdapter;
    }

    @Bean("moviesLocalBean")
    public LocalContainerEntityManagerFactoryBean getMoviesFactoryBean(@Qualifier("moviesDS") DataSource dataSource, HibernateJpaVendorAdapter  adapter)
    {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean=new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(adapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("moviesLocalBean");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean("albumsLocalBean")
    public LocalContainerEntityManagerFactoryBean getAlbumsFactoryBean(@Qualifier("albumsDS") DataSource dataSource, HibernateJpaVendorAdapter  adapter)
    {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean=new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(adapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("albumsLocalBean");

        return localContainerEntityManagerFactoryBean;
    }
    @Bean("moviesTM")
    public PlatformTransactionManager getMoviesTransactionManager(@Qualifier("moviesLocalBean")LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return transactionManager;
    }

    @Bean("albumsTM")
    public PlatformTransactionManager getAlbumsTransactionManager(@Qualifier("albumsLocalBean")LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return transactionManager;
    }
}
