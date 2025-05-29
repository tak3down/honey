/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'export',
  reactStrictMode: true,
  images: {
    domains: ['flagcdn.com', 'restcountries.com']
  }
}

module.exports = nextConfig
