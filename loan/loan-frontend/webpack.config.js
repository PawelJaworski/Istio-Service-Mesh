var path = require('path')

module.exports = {
  output: {
    path: path.resolve(__dirname, 'dist/loan-frontend'),
    filename: 'bundle.js'
  },
  resolve: {
    modules: [
      path.resolve(__dirname, 'node_modules'),
      path.resolve(__dirname, 'src/js'),
      path.resolve(__dirname, 'src/ts')
    ],
    extensions: ['.ts', '.tsx', '.js', '.json']
  },
  module: {
    rules: [{
      test: /\.(tsx?)|(js)$/,
      exclude: /node_modules/,
      loader: "babel-loader"
    }]
  },
}