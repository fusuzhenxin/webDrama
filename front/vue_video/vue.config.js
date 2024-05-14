const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true
})


module.exports = {
  chainWebpack: (config) => {
    config.module
        .rule('vue')
        .use('vue-loader')
        .loader('vue-loader')
        .tap(options => {
          options.compilerOptions = {
            ...options.compilerOptions,
            isCustomElement: tag => tag.startsWith('el-')
          }
          return options;
        });

    config.plugin('define').tap((args) => {
      const defineArgs = args[0];
      defineArgs['__VUE_PROD_HYDRATION_MISMATCH_DETAILS__'] = true;
      return [defineArgs];
    });
  },
};
