'use client';

import React, {useState} from 'react';

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

// Mock data for demo purposes since API is not available
const mockQuestions = [
  {
    flagUrl: "https://flagcdn.com/w320/us.png",
    correctCountry: "United States",
    options: ["United States", "Canada", "Mexico", "Brazil"],
    questionNumber: 1
  },
  {
    flagUrl: "https://flagcdn.com/w320/fr.png",
    correctCountry: "France",
    options: ["France", "Italy", "Spain", "Germany"],
    questionNumber: 2
  },
  {
    flagUrl: "https://flagcdn.com/w320/jp.png",
    correctCountry: "Japan",
    options: ["South Korea", "China", "Japan", "Thailand"],
    questionNumber: 3
  }
];
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
  const [userRank, setUserRank] = useState<number | null>(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);

  const authenticateUser = async () => {
    setLoading(true);
    // Mock authentication
    setTimeout(() => {
      if (username && password) {
        setUser({username});
        setCurrentPage('menu');
      } else {
        alert('Please enter both username and password');
      }
      setLoading(false);
    }, 1000);
  };

  const startGame = async () => {
    if (!user) {
      setCurrentPage('auth');
      return;
    }

    setLoading(true);
    // Mock game start
    setTimeout(() => {
      const session: GameSession = {
        sessionId: 'mock-session-' + Date.now(),
        score: 0,
        totalQuestions: 20,
        startTime: Date.now(),
        currentQuestion: {...mockQuestions[0]},
        isFinished: false
      };
      setGameSession(session);
      setCurrentQuestionIndex(0);
      setCurrentPage('game');
      setLoading(false);
    }, 1000);
  };

  const submitAnswer = async (answer: string) => {
    if (!gameSession || selectedAnswer) return;

    setSelectedAnswer(answer);
    setShowResult(true);

    const isCorrect = answer === gameSession.currentQuestion.correctCountry;

    setTimeout(() => {
      if (currentQuestionIndex >= 2) { // Demo with 3 questions
        // Game finished
        const finalScore = gameSession.score + (isCorrect ? 1 : 0);
        const updatedSession = {
          ...gameSession,
          score: finalScore,
          isFinished: true
        };
        setGameSession(updatedSession);

        // Add to leaderboard
        const timeElapsed = Date.now() - gameSession.startTime;
        const newEntry = {
          username: user!.username,
          score: finalScore,
          timeElapsed,
          completedAt: new Date().toISOString()
        };
        setLeaderboard(prev => [...prev, newEntry].sort((a, b) => {
          if (b.score !== a.score) return b.score - a.score;
          return a.timeElapsed - b.timeElapsed;
        }));

        const rank = leaderboard.findIndex(entry => entry.username === user!.username) + 1;
        setUserRank(rank > 0 ? rank : leaderboard.length + 1);

        setCurrentPage('results');
      } else {
        // Next question
        const nextIndex = currentQuestionIndex + 1;
        const nextQuestion = {...mockQuestions[nextIndex], questionNumber: nextIndex + 1};
        setGameSession({
          ...gameSession,
          score: gameSession.score + (isCorrect ? 1 : 0),
          currentQuestion: nextQuestion
        });
        setCurrentQuestionIndex(nextIndex);
        setSelectedAnswer(null);
        setShowResult(false);
      }
    }, 1500);
  };

  const loadLeaderboard = async (silent = false) => {
    // Mock load leaderboard
    if (!silent) {
      setCurrentPage('ranking');
    }
  };

  const formatTime = (milliseconds: number) => {
    const seconds = Math.floor(milliseconds / 1000);
    const minutes = Math.floor(seconds / 60);
    return `${minutes}:${(seconds % 60).toString().padStart(2, '0')}`;
  };

  const logout = () => {
    setUser(null);
    setGameSession(null);
    setCurrentPage('menu');
    setUsername('');
    setPassword('');
  };

  // Navbar Component
  const Navbar = () => (
      <nav className="bg-gray-900 border-b border-gray-700 sticky top-0 z-50 shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-4">
              <h1 className="text-2xl font-bold text-blue-400">🏴 Flag Master</h1>
            </div>

            <div className="flex items-center space-x-6">
              {user && (
                  <>
                    <span className="text-gray-300 font-medium">Welcome, {user.username}</span>
                    <div className="flex space-x-3">
                      <button
                          onClick={() => setCurrentPage('menu')}
                          className="text-gray-300 hover:text-white px-3 py-2 rounded-lg hover:bg-gray-700 transition-all duration-200"
                      >
                        Home
                      </button>
                      <button
                          onClick={() => loadLeaderboard()}
                          className="text-gray-300 hover:text-white px-3 py-2 rounded-lg hover:bg-gray-700 transition-all duration-200"
                      >
                        Leaderboard
                      </button>
                      <button
                          onClick={logout}
                          className="bg-red-600 text-white hover:bg-red-700 px-4 py-2 rounded-lg transition-all duration-200 border border-red-500"
                      >
                        Logout
                      </button>
                    </div>
                  </>
              )}
              {!user && currentPage !== 'auth' && (
                  <button
                      onClick={() => setCurrentPage('auth')}
                      className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-medium transition-all duration-200 shadow-lg"
                  >
                    Sign In
                  </button>
              )}
            </div>
          </div>
        </div>
      </nav>
  );

  const renderMenu = () => (
      <div className="min-h-screen bg-gray-900">
        <Navbar/>
        <div className="flex items-center justify-center min-h-screen px-4" style={{minHeight: 'calc(100vh - 4rem)'}}>
          <div className="bg-gray-800 rounded-2xl shadow-2xl border border-gray-700 p-12 max-w-lg w-full">
            <div className="text-center mb-12">
              <div className="text-6xl mb-4">🌍</div>
              <h2 className="text-4xl font-bold text-blue-400 mb-4">
                Flag Quiz Challenge
              </h2>
              <p className="text-gray-400 text-lg">Test your geography knowledge with flags from around the world</p>
            </div>

            <div className="space-y-4">
              <button
                  onClick={startGame}
                  disabled={loading}
                  className="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg transform hover:scale-105 disabled:opacity-50 disabled:transform-none"
              >
                {loading ? (
                    <div className="flex items-center justify-center space-x-2">
                      <div
                          className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      <span>Loading...</span>
                    </div>
                ) : (
                    '🎮 START GAME'
                )}
              </button>

              <button
                  onClick={() => loadLeaderboard()}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg transform hover:scale-105"
              >
                🏆 VIEW LEADERBOARD
              </button>
            </div>
        </div>
      </div>
    </div>
  );

  const renderAuth = () => (
      <div className="min-h-screen bg-gray-900">
        <Navbar/>
        <div className="flex items-center justify-center px-4" style={{minHeight: 'calc(100vh - 4rem)'}}>
          <div className="bg-gray-800 rounded-2xl shadow-2xl border border-gray-700 p-10 max-w-md w-full">
            <div className="text-center mb-8">
              <h2 className="text-3xl font-bold text-white mb-2">
                {isLogin ? 'Welcome Back' : 'Join Flag Master'}
              </h2>
              <p className="text-gray-400">
                {isLogin ? 'Sign in to continue your journey' : 'Create your account to start playing'}
              </p>
            </div>

            <div className="space-y-6">
              <div>
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                />
              </div>

              <div>
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                />
              </div>

              <button
                  onClick={authenticateUser}
                  disabled={loading || !username || !password}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-xl transition-all duration-200 shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? 'Processing...' : (isLogin ? 'Sign In' : 'Create Account')}
              </button>

              <button
                  onClick={() => setIsLogin(!isLogin)}
                  className="w-full text-gray-400 hover:text-white font-medium transition-colors duration-200"
              >
                {isLogin ? "Don't have an account? Sign up" : 'Already have an account? Sign in'}
              </button>
            </div>
        </div>
      </div>
    </div>
  );

  const renderGame = () => {
    if (!gameSession) return null;

    const { currentQuestion, score, totalQuestions } = gameSession;
    const isCorrect = selectedAnswer === currentQuestion.correctCountry;

    return (
        <div className="min-h-screen bg-gray-900">
          <Navbar/>
          <div className="py-8 px-4" style={{minHeight: 'calc(100vh - 4rem)'}}>
            <div className="max-w-4xl mx-auto">
              <div className="bg-gray-800 rounded-2xl shadow-2xl border border-gray-700 p-8">

                {/* Game Header */}
                <div className="flex justify-between items-center mb-8">
                  <div className="flex items-center space-x-4">
                    <div className="bg-blue-600 text-white px-4 py-2 rounded-lg font-bold">
                      Question {currentQuestion.questionNumber} / 3
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-2xl font-bold text-white">Score: {score}</div>
                    <div
                        className="text-gray-400">Accuracy: {Math.round((score / currentQuestion.questionNumber) * 100)}%
                    </div>
                  </div>
                </div>

                {/* Progress Bar */}
                <div className="mb-8">
                  <div className="bg-gray-700 rounded-full h-3">
                    <div
                        className="bg-blue-500 h-3 rounded-full transition-all duration-500"
                        style={{width: `${(currentQuestion.questionNumber / 3) * 100}%`}}
                    ></div>
                  </div>
                </div>

                {/* Flag Display */}
                <div className="text-center mb-10">
                  <div className="inline-block bg-gray-700 p-4 rounded-2xl border border-gray-600 shadow-xl">
                    <img
                        src={currentQuestion.flagUrl}
                        alt="Country flag"
                        className="w-80 h-52 object-cover rounded-xl shadow-lg"
                    />
                  </div>
                  <p className="text-gray-300 mt-4 text-lg">Which country does this flag belong to?</p>
                </div>

                {/* Answer Options */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {currentQuestion.options.map((option, index) => {
                    let buttonClass = "w-full py-4 px-6 rounded-xl font-semibold text-lg transition-all duration-200 border-2 transform hover:scale-105 ";

                    if (showResult && selectedAnswer) {
                      if (option === currentQuestion.correctCountry) {
                        buttonClass += "bg-green-600 text-white border-green-500 shadow-lg";
                      } else if (option === selectedAnswer && !isCorrect) {
                        buttonClass += "bg-red-600 text-white border-red-500 shadow-lg";
                      } else {
                        buttonClass += "bg-gray-700 text-gray-400 border-gray-600";
                      }
                    } else {
                      buttonClass += "bg-gray-700 hover:bg-blue-600 text-white border-gray-600 hover:border-blue-500 shadow-lg";
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
      </div>
    );
  };

  const renderResults = () => {
    if (!gameSession) return null;

    const timeElapsed = Date.now() - gameSession.startTime;
    const accuracy = Math.round((gameSession.score / 3) * 100);

    return (
        <div className="min-h-screen bg-gray-900">
          <Navbar/>
          <div className="flex items-center justify-center px-4" style={{minHeight: 'calc(100vh - 4rem)'}}>
            <div className="bg-gray-800 rounded-2xl shadow-2xl border border-gray-700 p-12 max-w-lg w-full">

              {/* Results Header */}
              <div className="text-center mb-8">
                <div className="text-6xl mb-4">
                  {accuracy >= 90 ? '🏆' : accuracy >= 70 ? '🥈' : accuracy >= 50 ? '🥉' : '📊'}
                </div>
                <h2 className="text-4xl font-bold text-blue-400 mb-2">
                  Game Complete!
                </h2>
              </div>

              {/* Score Display */}
              <div className="text-center mb-8">
                <div className="bg-gray-700 rounded-2xl p-6 border border-gray-600 mb-6">
                  <div className="text-6xl font-bold text-green-400 mb-2">
                    {gameSession.score}/3
                  </div>
                  <div className="text-gray-300 text-xl mb-2">
                    Time: {formatTime(timeElapsed)}
                  </div>
                  <div className="text-gray-400 text-lg">
                    Accuracy: {accuracy}%
                  </div>
                </div>

                {/* Rank Display */}
                {userRank && (
                    <div className="bg-blue-900 border border-blue-700 rounded-xl p-4 mb-6">
                      <div className="text-lg text-blue-300 font-semibold">
                        🎯 Your Global Rank: #{userRank}
                      </div>
                    </div>
                )}
              </div>

              {/* Action Buttons */}
              <div className="space-y-4">
                <button
                    onClick={() => loadLeaderboard()}
                    className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg transform hover:scale-105"
                >
                  🏆 View Full Leaderboard
                </button>

                <button
                    onClick={startGame}
                    className="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg transform hover:scale-105"
                >
                  🎮 Play Again
                </button>

                <button
                    onClick={() => setCurrentPage('menu')}
                    className="w-full bg-gray-600 hover:bg-gray-700 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg"
                >
                  🏠 Main Menu
                </button>
              </div>
          </div>
        </div>
      </div>
    );
  };

  const renderRanking = () => (
      <div className="min-h-screen bg-gray-900">
        <Navbar/>
        <div className="py-8 px-4" style={{minHeight: 'calc(100vh - 4rem)'}}>
          <div className="max-w-4xl mx-auto">
            <div className="bg-gray-800 rounded-2xl shadow-2xl border border-gray-700 p-8">
              <div className="text-center mb-8">
                <h2 className="text-4xl font-bold text-yellow-400 mb-2">
                  🏆 Global Leaderboard
                </h2>
                <p className="text-gray-400">Top players from around the world</p>
              </div>

              <div className="space-y-3">
                {leaderboard.map((entry, index) => {
                  const isCurrentUser = user && entry.username === user.username;

                  return (
                      <div
                          key={index}
                          className={`flex justify-between items-center p-6 rounded-xl transition-all duration-200 border ${
                              index === 0 ? 'bg-yellow-900 border-yellow-700' :
                                  index === 1 ? 'bg-gray-700 border-gray-600' :
                                      index === 2 ? 'bg-orange-900 border-orange-700' :
                                          isCurrentUser ? 'bg-blue-900 border-blue-700' :
                                              'bg-gray-700 border-gray-600'
                          }`}
                      >
                        <div className="flex items-center space-x-4">
                          <div className={`w-12 h-12 rounded-full flex items-center justify-center font-bold text-lg ${
                              index === 0 ? 'bg-yellow-500 text-white' :
                                  index === 1 ? 'bg-gray-500 text-white' :
                                      index === 2 ? 'bg-orange-500 text-white' :
                                          'bg-gray-600 text-gray-200'
                          }`}>
                            {index < 3 ? ['🥇', '🥈', '🥉'][index] : `#${index + 1}`}
                          </div>
                          <div>
                            <div className={`font-bold text-lg ${isCurrentUser ? 'text-blue-300' : 'text-white'}`}>
                              {entry.username} {isCurrentUser && '(You)'}
                            </div>
                            <div className="text-gray-400 text-sm">
                              {new Date(entry.completedAt).toLocaleDateString()}
                            </div>
                          </div>
                        </div>
                        <div className="text-right">
                          <div
                              className="text-2xl font-bold text-green-400">{entry.score}/{entry.score <= 3 ? '3' : '20'}</div>
                          <div className="text-sm text-gray-400">{formatTime(entry.timeElapsed)}</div>
                          <div
                              className="text-xs text-gray-500">{Math.round((entry.score / (entry.score <= 3 ? 3 : 20)) * 100)}%
                            accuracy
                          </div>
                        </div>
                      </div>
                  );
                })}
              </div>

              {leaderboard.length === 0 && (
                  <div className="text-center py-12">
                    <div className="text-4xl mb-4">🎯</div>
                    <p className="text-gray-400 text-lg">No games completed yet. Be the first!</p>
              </div>
              )}
          </div>
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