import './globals.css'
import type {Metadata} from 'next'
import React from "react";

export const metadata: Metadata = {
  title: 'Honey',
  description: 'Sprawdź swoją wiedzę geograficzną z flagami z całego świata',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
      <html lang="en" className="h-full">
      <body className="min-h-full bg-gray-900 text-white">{children}</body>
    </html>
  )
}
