'use client';

import React, { useState, useEffect } from 'react';

interface User {
  username: string;
  sessionId?: string;
}

interface GameQuestion {
  flagUrl: string;
  correctCountry: string;
  options: string[];
  questionNumber: number;
}

interface GameSession {
  sessionId: string;
  score: number;
  totalQuestions: number;
  startTime: number;
  currentQuestion: GameQuestion;
  isFinished: boolean;
}

interface LeaderboardEntry {
  username: string;
  score: number;
  timeElapsed: number;
  completedAt: string;
}

const API_BASE = 'http://localhost:8080/api';

export default function FlagGame() {
  const [currentPage, setCurrentPage] = useState<'menu' | 'auth' | 'game' | 'results' | 'ranking'>('menu');
  const [user, setUser] = useState<User | null>(null);
  const [gameSession, setGameSession] = useState<GameSession | null>(null);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLogin, setIsLogin] = useState(true);
  const [selectedAnswer, setSelectedAnswer] = useState<string | null>(null);
  const [showResult, setShowResult] = useState(false);
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [loading, setLoading] = useState(false);

  const authenticateUser = async () => {
    setLoading(true);
    try {
      const endpoint = isLogin ? '/auth/login' : '/auth/register';
      const response = await fetch(`${API_BASE}${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const userData = await response.json();
        setUser(userData);
        setCurrentPage('menu');
      } else {
        alert(isLogin ? 'Invalid credentials' : 'Username already exists');
      }
    } catch (error) {
      alert('Authentication failed');
    }
    setLoading(false);
  };

  const startGame = async () => {
    if (!user) {
      setCurrentPage('auth');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/game/start`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: user.username }),
      });

      if (response.ok) {
        const session = await response.json();
        setGameSession(session);
        setCurrentPage('game');
      }
    } catch (error) {
      alert('Failed to start game');
    }
    setLoading(false);
  };

  const submitAnswer = async (answer: string) => {
    if (!gameSession || selectedAnswer) return;

    setSelectedAnswer(answer);
    setShowResult(true);

    try {
      const response = await fetch(`${API_BASE}/game/answer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sessionId: gameSession.sessionId,
          answer: answer,
        }),
      });

      if (response.ok) {
        const updatedSession = await response.json();

        setTimeout(() => {
          if (updatedSession.isFinished) {
            setGameSession(updatedSession);
            setCurrentPage('results');
          } else {
            setGameSession(updatedSession);
            setSelectedAnswer(null);
            setShowResult(false);
          }
        }, 1500);
      }
    } catch (error) {
      alert('Failed to submit answer');
    }
  };

  const loadLeaderboard = async () => {
    try {
      const response = await fetch(`${API_BASE}/ranking`);
      if (response.ok) {
        const data = await response.json();
        setLeaderboard(data);
        setCurrentPage('ranking');
      }
    } catch (error) {
      alert('Failed to load leaderboard');
    }
  };

  const formatTime = (milliseconds: number) => {
    const seconds = Math.floor(milliseconds / 1000);
    const minutes = Math.floor(seconds / 60);
    return `${minutes}:${(seconds % 60).toString().padStart(2, '0')}`;
  };

  const renderMenu = () => (
    <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
      <div className="bg-white rounded-lg shadow-2xl p-8 max-w-md w-full mx-4">
        <h1 className="text-4xl font-bold text-center mb-8 text-gray-800">Flag Quiz</h1>
        <div className="space-y-4">
          <button
            onClick={startGame}
            disabled={loading}
            className="w-full bg-green-500 hover:bg-green-600 text-white font-bold py-4 px-6 rounded-lg transition-colors disabled:opacity-50"
          >
            {loading ? 'Loading...' : 'PLAY'}
          </button>
          <button
            onClick={loadLeaderboard}
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-4 px-6 rounded-lg transition-colors"
          >
            RANKING
          </button>
        </div>
      </div>
    </div>
  );

  const renderAuth = () => (
    <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
      <div className="bg-white rounded-lg shadow-2xl p-8 max-w-md w-full mx-4">
        <h2 className="text-3xl font-bold text-center mb-6 text-gray-800">
          {isLogin ? 'Login' : 'Register'}
        </h2>
        <div className="space-y-4">
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            onClick={authenticateUser}
            disabled={loading || !username || !password}
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50"
          >
            {loading ? 'Loading...' : (isLogin ? 'Login' : 'Register')}
          </button>
          <button
            onClick={() => setIsLogin(!isLogin)}
            className="w-full text-blue-500 hover:text-blue-600 font-medium"
          >
            {isLogin ? 'Need an account? Register' : 'Have an account? Login'}
          </button>
          <button
            onClick={() => setCurrentPage('menu')}
            className="w-full text-gray-500 hover:text-gray-600"
          >
            Back to Menu
          </button>
        </div>
      </div>
    </div>
  );

  const renderGame = () => {
    if (!gameSession) return null;

    const { currentQuestion, score, totalQuestions } = gameSession;
    const isCorrect = selectedAnswer === currentQuestion.correctCountry;

    return (
      <div className="min-h-screen bg-gradient-to-br from-green-400 to-blue-500 py-8">
        <div className="max-w-4xl mx-auto px-4">
          <div className="bg-white rounded-lg shadow-2xl p-8">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-800">Flag Quiz</h2>
              <div className="text-lg font-semibold text-gray-600">
                Question {currentQuestion.questionNumber} / 20 | Score: {score}
              </div>
            </div>

            <div className="text-center mb-8">
              <div className="inline-block border-4 border-gray-300 rounded-lg overflow-hidden shadow-lg">
                <img
                  src={currentQuestion.flagUrl}
                  alt="Country flag"
                  className="w-64 h-40 object-cover"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {currentQuestion.options.map((option, index) => {
                let buttonClass = "w-full py-4 px-6 rounded-lg font-semibold text-lg transition-all ";

                if (showResult && selectedAnswer) {
                  if (option === currentQuestion.correctCountry) {
                    buttonClass += "bg-green-500 text-white";
                  } else if (option === selectedAnswer && !isCorrect) {
                    buttonClass += "bg-red-500 text-white";
                  } else {
                    buttonClass += "bg-gray-300 text-gray-600";
                  }
                } else {
                  buttonClass += "bg-blue-500 hover:bg-blue-600 text-white";
                }

                return (
                  <button
                    key={index}
                    onClick={() => submitAnswer(option)}
                    disabled={showResult}
                    className={buttonClass}
                  >
                    {option}
                  </button>
                );
              })}
            </div>
          </div>
        </div>
      </div>
    );
  };

  const renderResults = () => {
    if (!gameSession) return null;

    const timeElapsed = Date.now() - gameSession.startTime;

    return (
      <div className="min-h-screen bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center">
        <div className="bg-white rounded-lg shadow-2xl p-8 max-w-md w-full mx-4">
          <h2 className="text-3xl font-bold text-center mb-6 text-gray-800">Game Complete!</h2>
          <div className="text-center space-y-4">
            <div className="text-6xl font-bold text-green-500">{gameSession.score}/20</div>
            <div className="text-xl text-gray-600">
              Time: {formatTime(timeElapsed)}
            </div>
            <div className="text-lg text-gray-600">
              Accuracy: {Math.round((gameSession.score / 20) * 100)}%
            </div>
          </div>
          <div className="space-y-4 mt-8">
            <button
              onClick={loadLeaderboard}
              className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-3 px-6 rounded-lg transition-colors"
            >
              View Ranking
            </button>
            <button
              onClick={startGame}
              className="w-full bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-6 rounded-lg transition-colors"
            >
              Play Again
            </button>
            <button
              onClick={() => setCurrentPage('menu')}
              className="w-full bg-gray-500 hover:bg-gray-600 text-white font-bold py-3 px-6 rounded-lg transition-colors"
            >
              Main Menu
            </button>
          </div>
        </div>
      </div>
    );
  };

  const renderRanking = () => (
    <div className="min-h-screen bg-gradient-to-br from-yellow-400 to-red-500 py-8">
      <div className="max-w-4xl mx-auto px-4">
        <div className="bg-white rounded-lg shadow-2xl p-8">
          <h2 className="text-3xl font-bold text-center mb-8 text-gray-800">Leaderboard</h2>
          <div className="space-y-4">
            {leaderboard.map((entry, index) => (
              <div
                key={index}
                className={`flex justify-between items-center p-4 rounded-lg ${
                  index === 0 ? 'bg-yellow-100 border-2 border-yellow-400' :
                  index === 1 ? 'bg-gray-100 border-2 border-gray-400' :
                  index === 2 ? 'bg-orange-100 border-2 border-orange-400' :
                  'bg-blue-50'
                }`}
              >
                <div className="flex items-center space-x-4">
                  <div className="text-2xl font-bold text-gray-600">#{index + 1}</div>
                  <div className="font-semibold text-lg">{entry.username}</div>
                </div>
                <div className="text-right">
                  <div className="text-xl font-bold text-green-600">{entry.score}/20</div>
                  <div className="text-sm text-gray-500">{formatTime(entry.timeElapsed)}</div>
                </div>
              </div>
            ))}
          </div>
          <button
            onClick={() => setCurrentPage('menu')}
            className="w-full mt-8 bg-blue-500 hover:bg-blue-600 text-white font-bold py-3 px-6 rounded-lg transition-colors"
          >
            Back to Menu
          </button>
        </div>
      </div>
    </div>
  );

  switch (currentPage) {
    case 'menu': return renderMenu();
    case 'auth': return renderAuth();
    case 'game': return renderGame();
    case 'results': return renderResults();
    case 'ranking': return renderRanking();
    default: return renderMenu();
  }
}