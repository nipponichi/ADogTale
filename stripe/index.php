<?php

include(__DIR__.'/vendor/autoload.php');

use Stripe\StripeClient;

$stripe = new StripeClient('sk_test_51PIZY3Dk9aU2RFwtfrqt9f6ADvnxV43TcjpesuHdDoOusYf4XTnRBwxbnECpsCQ6CLwZnU6c3hnkCFeE6icCeMLx002vkJR2uU');

$customer = $stripe->customers->create([
    'name' => 'fakeCustomer',
    'address' => [
        'line1' => 'citli 2G',
        'postal_code' => '02120',
        'city' => 'Azcapotzalco',
        'state' => 'CDMX',
        'country' => 'Mexico'
    ]
]);

$ephemeralKey = $stripe->ephemeralKeys->create([
    'customer' => $customer->id,
], [
    'stripe_version' => '2024-04-10',
]);
$paymentIntent = $stripe->paymentIntents->create([
    'amount' => 1,
    'currency' => 'eur',
    'customer' => $customer->id,
    'description' => 'A Dog Tail Subscription for no advertisments',
    'automatic_payment_methods' => [
        'enabled' => 'true',
    ],
]);

$response = json_encode([
    'paymentIntent' => $paymentIntent->client_secret,
    'ephemeralKey' => $ephemeralKey->secret,
    'customer' => $customer->id,
    'publishableKey' => 'pk_test_51PIZY3Dk9aU2RFwtNPsH8HfckE2SGgLA841wk6eYPvPhfDxITjMN7dfYVyLzkQ2OySEXq30gFp2W3zUdRudYurkF00ThtvNf6k'
]);

header('Content-Type: application/json');

echo($response);

