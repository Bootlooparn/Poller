import webpack from 'webpack'
import HtmlwebpackPlugin from 'html-webpack-plugin'
import CssMinimizerPlugin from 'css-minimizer-webpack-plugin'
import CompressionPlugin from 'compression-webpack-plugin'

const configuration: webpack.Configuration = {
    devtool: "source-map",
    mode: 'production',
    entry: './src/app.tsx',
    output: {
        filename: 'bundle.tsx',
        path: __dirname + '/dist'
    },
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
    resolve: {
        extensions: [".ts", ".tsx", ".js", ".jsx", ".scss", "sass"]
    },
    plugins: [
        new HtmlwebpackPlugin({
            template: './template/index.html',
            title: 'KRY'
        }),
        new webpack.DefinePlugin({
            'process.env': {
                'NODE_ENV': JSON.stringify('production')
            }
        }),
        new CompressionPlugin({
            algorithm: "gzip",
            test: /\.(tsx$|js$)/,
            threshold: 10240,
            minRatio: 0.8
        })
    ],
    optimization: {
        minimizer: [ new CssMinimizerPlugin() ]
    }
}

export default configuration