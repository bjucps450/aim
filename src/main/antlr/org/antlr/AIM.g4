grammar AIM;

@header {
    package org.antlr;
}

/**
  * Parser Rules
   */

start: (primary LINE_SEP)+ EOF;

primary: method # meth
        | unconditional_primary # un;

unconditional_primary: perchance # might
                      | assignment # assign
                      | yeet # ye
                      | CALL IDENTIFIER (SPLIT args+=expression)+ BANG # call_meth;

multi_unconditional_primary: (unconditional_primary LINE_SEP)+;

assignment: value=expression ASSIGNMENT IDENTIFIER;

yeet: YEET value=expression;

perchance: QUESTION expression BACKTICK LINE_SEP yee=multi_unconditional_primary BACKTICK (LINE_SEP ELSE BACKTICK LINE_SEP nah=multi_unconditional_primary BACKTICK)*;

method:  FED name=IDENTIFIER (SPLIT args+=argument)+ BACKTICK LINE_SEP multi_unconditional_primary BACKTICK BANG # with_args
       | HUNGRY name=IDENTIFIER BACKTICK LINE_SEP multi_unconditional_primary BACKTICK BANG # no_args;

argument: IDENTIFIER COLON type;

type: INT # notstr | STR # notint;

expression: PARENS expression PARENS # paren
          | NEGATIVE expression # neg
          | first=expression op=(MULTIPLY | DIVIDE) second=expression # md
          | first=expression op=(PLUS | SUB) second=expression # ps
          | first=expression PLUSPLUS second=expression # concat
          | CALL IDENTIFIER (SPLIT args+=expression)+ BANG # call
          | INTEGER_LITERAL # int
          | IDENTIFIER # id;

/**
  * Lexer Rules
   */

fragment VALID_ID_START: ('a' .. 'z') | ('A' .. 'Z') | '_';
fragment VALID_ID_CHAR: VALID_ID_START | ('0' .. '9');
fragment LINE_FEED_CARRIAGE_RETURN: [\u000A\u000D];
fragment SPACE_AND_TAB: [\u0009\u0020];

INTEGER_LITERAL: ('0' .. '9')+;
CALL: 'call';
HUNGRY: 'hungry';
FED: 'fed';
ELSE: 'els';
YEET: 'yeet';
PLUSPLUS: 'plusplus';
COLON: 'typeis';
INT: 'notstr';
STR: 'notint';
ASSIGNMENT: '=:';
NEGATIVE: '0>';
LINE_SEP: '~';
PLUS: '+';
SUB: '-';
MULTIPLY: '*';
DIVIDE: '/';
PARENS: '&';
BANG: '!';
BACKTICK: '`';
QUESTION: '?';
SPLIT: '|';

IDENTIFIER: VALID_ID_START VALID_ID_CHAR*;
WHITESPACE: SPACE_AND_TAB -> skip;
NEWLINE: LINE_FEED_CARRIAGE_RETURN -> skip;