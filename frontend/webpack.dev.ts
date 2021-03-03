import webpack from 'webpack'
import HtmlwebpackPlugin from 'html-webpack-plugin'

const configuration: webpack.Configuration = {
    mode: 'development',
    entry: './src/app.tsx',
    module: {
        rules: [{
            test: /\.tsx$/,
            use: {
                "loader": "ts-loader",
                "options": {
                    "transpileOnly": true
                }
            },
            exclude: /node_modules/
        },
        {
            test: /\.(css|scss)$/,
            use: ['style-loader', 'css-loader', 'sass-loader']
        }]
    },
    devServer: {
        historyApiFallback: true,
        compress: true,
        open: true,
        hot: true,
        port: 8080
    },
    resolve: {
        extensions: [".ts", ".tsx", ".js", ".jsx", ".scss", "sass"]
    },
    plugins: [
        new webpack.HotModuleReplacementPlugin(),
        new HtmlwebpackPlugin({
            template: './template/index.html',
            title: 'KRY'
        })
    ]
}

export default configuration